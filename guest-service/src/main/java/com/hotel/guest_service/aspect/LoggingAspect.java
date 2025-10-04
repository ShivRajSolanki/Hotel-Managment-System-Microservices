package com.hotel.guest_service.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
   // Match all methods in any class inside the com.hotel.guest_service.service package
    @Pointcut("execution(* com.hotel.guest_service.service..*(..))")
    public void serviceMethods() {}

//This logs the method name and its arguments before the method runs.
    @Before("serviceMethods()")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering method: {} with arguments: {}",
                joinPoint.getSignature().toShortString(),
                joinPoint.getArgs());
    }

//This logs the method name and its return value after it completes successfully.
    @AfterReturning(pointcut = "serviceMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Method {} returned: {}",
                joinPoint.getSignature().toShortString(),
                result);
    }

// This logs the method name and the exception if the method throws an error.
    @AfterThrowing(pointcut = "serviceMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        log.error("Method {} threw exception: {}",
                joinPoint.getSignature().toShortString(),
                ex.getMessage(), ex);
    }

//This logs when the method finishes, whether it succeeded or failed
    @After("serviceMethods()")
    public void logAfter(JoinPoint joinPoint) {
        log.info("Exiting method: {}", joinPoint.getSignature().toShortString());
    }
}