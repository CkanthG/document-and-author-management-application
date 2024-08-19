package com.krieger.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * To log all request and response messages.
 */
@Aspect
@Component
@Slf4j
public class LoggerAdvice {


    /**
     * Defines a pointcut that matches the execution of any method within the "com.krieger" package and its sub-packages.
     */
    @Pointcut(value = "execution(* com.krieger.*.*.*.*(..) )")
    public void loggerPointcut() {}

    /**
     * An advice method that is executed around the methods matched by the defined pointcut.
     *
     * @param proceedingJoinPoint provides access to the method being advised, allowing the advice to proceed with the method invocation.
     * @return the result of the method execution.
     * @throws Throwable allows any exception thrown by the advised method to be propagated.
     */
    @Around(value = "loggerPointcut()")
    public Object applicationLoggerAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        var methodName = proceedingJoinPoint.getSignature().getName();
        var className = proceedingJoinPoint.getTarget().getClass().getName();
        var params = proceedingJoinPoint.getArgs();
        log.debug("Class Name : {}, Method Name : {}(), Arguments : {}", className, methodName, params);
        var proceed = proceedingJoinPoint.proceed();
        log.debug("Class Name : {}, Method Name : {}(), Response : {}", className, methodName, proceed);
        return proceed;
    }
}
