package com.t1.openschool.atumanov.homework_aop.advice;

import com.t1.openschool.atumanov.homework_aop.model.MethodExecution;
import com.t1.openschool.atumanov.homework_aop.service.MethodExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
@Aspect
public class MethodExecutionTimeTrackingAspect {

    private final MethodExecutionService service;

    @Pointcut("@annotation(com.t1.openschool.atumanov.homework_aop.annotation.TrackTime)")
    public void trackTimeAnnotationPointcut() {}

    @Around("trackTimeAnnotationPointcut()")
    public Object aroundMethod(ProceedingJoinPoint proceedingJoinPoint) {
        String fullName = proceedingJoinPoint.getSignature().getDeclaringTypeName() + "." + proceedingJoinPoint.getSignature().getName();

        long start = System.nanoTime();
        Object result = null;
        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception during advice execution: {}", e.getMessage());
        }
        long end = System.nanoTime();
        long duration = end - start;

        service.save(new MethodExecution(fullName, duration, false))
                .subscribe(execution -> log.debug("Saved method execution data with an ID {}", execution.getId()),
                            exception -> log.error("Exception during saving: {}", exception.getMessage()));

        return result;
    }
}
