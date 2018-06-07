package eu.gobio.jape.tracker;

import eu.gobio.jape.ObserverWrapper;
import eu.gobio.jape.Trace;
import io.reactivex.Observable;
import io.reactivex.Observer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class RXJavaTracker extends AbstractTracker {
    @Pointcut("execution(* io.reactivex.plugins.RxJavaPlugins.onSubscribe(io.reactivex.Observable+,io.reactivex.Observer+))" +
            " && args(observable,observer)")
    public void onSubscribe(Observable observable, Observer observer) {
    }

    @Around("onSubscribe(observable,observer)")
    public Object aroundOnSubscribe(ProceedingJoinPoint jp, Observable observable, Observer observer) throws Throwable {
        return new ObserverWrapper<>((Observer) jp.proceed(), Trace.currentStage());
    }
}
