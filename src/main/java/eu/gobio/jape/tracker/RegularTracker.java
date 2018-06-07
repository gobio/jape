package eu.gobio.jape.tracker;

import eu.gobio.jape.Trace;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

@Aspect
public class RegularTracker extends AbstractTracker {
    @Pointcut("execution(@eu.gobio.jape.Track * *(..))")
    public void trackPointcut(){
    }

    @Around("trackPointcut()")
    public Object aroundTracked(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature ms = (MethodSignature)joinPoint.getSignature();
        Trace.startStage(ms.getName());
        try{
            return joinPoint.proceed();
        }finally {
            Trace.finishStage();
        }
    }

}
