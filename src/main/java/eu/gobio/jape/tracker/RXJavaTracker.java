package eu.gobio.jape.tracker;

import eu.gobio.jape.*;
import eu.gobio.jape.rxjava.Assembly;
import eu.gobio.jape.rxjava.Subscription;
import io.reactivex.Observable;
import io.reactivex.Observer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

@Aspect
public class RXJavaTracker extends AbstractTracker {
    private static Map<Observable, Assembly> observableAssemblies = Collections.synchronizedMap(new WeakHashMap<>());
    private static Map<Observer, Subscription> observerSubscriptions = Collections.synchronizedMap(new WeakHashMap<>());


    @Around("call(* io.reactivex.plugins.RxJavaPlugins.onAssembly(io.reactivex.Observable+))" +
            " && args(currentObservable)")
    public Object assembly(ProceedingJoinPoint jp, Observable currentObservable) throws Throwable {
        Object upstreamObservable = jp.getThis();

        storeAssembly(currentObservable, upstreamObservable);

        return jp.proceed();
    }

    private void storeAssembly(Observable currentObservable, Object upstreamObservable) {
        Assembly upstreamAssembly = observableAssemblies.get(upstreamObservable);
        Assembly currentAssembly = new Assembly(currentObservable, upstreamAssembly);
        currentAssembly.setStage(Trace.currentStage());
        observableAssemblies.put(currentObservable, currentAssembly);
    }


    @Around("call(* io.reactivex.ObservableSource.subscribe(io.reactivex.Observer+)) && args(observer)")
    public Object subscribe(ProceedingJoinPoint jp, Observer observer) throws Throwable {
        Object observable = jp.getTarget();

        Assembly assembly = observableAssemblies.get(observable);
        Subscription subscription = new Subscription(observer, assembly, null);
        subscription.setStage(Trace.currentStage());
        observerSubscriptions.put(observer, subscription);

        return jp.proceed();
    }

    @Around("call(* io.reactivex.Observer.onNext(Object+)) && args(value)")
    public Object next(ProceedingJoinPoint jp, Object value) throws Throwable {
        // upstream -> current
        Object upstreamObserver = jp.getThis();
        Object currentObserver = jp.getTarget();

        Stage upstreamStage = findUpstreamStage(upstreamObserver);

        Subscription currentSubscription = observerSubscriptions.get(currentObserver);
        Stage subscriptionStage = currentSubscription != null ? currentSubscription.getStage() : null;
        Stage assemblyStage = currentSubscription != null ? currentSubscription.getAssembly().getStage() : null;
        Stage parentStage = subscriptionStage != null ? subscriptionStage : assemblyStage;
        return callInTrace(parentStage, () -> {
            Stage ephemeralStage = Trace.startEphemeralStage(generateObserverSignature(currentObserver), new Flow
                    (upstreamStage, value
                    .toString()));
            if (currentSubscription != null) {
                currentSubscription.setFlow(ephemeralStage);
            }
            try {
                return jp.proceed();
            } finally {
                Trace.finishStage(ephemeralStage);
            }
        });

    }

    private Stage findUpstreamStage(Object observer) {
        Subscription upstreamSubscription = observerSubscriptions.get(observer);
        if (upstreamSubscription != null) {
            return upstreamSubscription.getFlow();
        }
        return null;
    }


    private Object callInTrace(Stage stage, ThrowableCallable callable) throws Throwable {
        boolean externalStageChanged = false;
        try {
            if (!Trace.inTransaction() && stage != null) {
                Trace.setExternalStage(stage);
                externalStageChanged = true;
            }
            return callable.call();
        } finally {
            if (externalStageChanged) {
                Trace.setExternalStage(null);
            }

        }
    }

    private String generateObserverSignature(Object currentObserver) {
        return "rx" + currentObserver.getClass().getSimpleName().replace("Observer", "");
    }


}
