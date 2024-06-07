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

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
@Aspect
public class MethodExecutionAsyncTimeTrackingAspect {

    private final MethodExecutionService service;

    @Pointcut("@annotation(com.t1.openschool.atumanov.homework_aop.annotation.TrackAsyncTime)")
    public void trackAsyncTimeAnnotationPointcut() {}

    @Around("trackAsyncTimeAnnotationPointcut()")
    @SuppressWarnings("unchecked")
    public Object aroundAsyncMethod(ProceedingJoinPoint proceedingJoinPoint) {
        final String fullName = proceedingJoinPoint.getSignature().getDeclaringTypeName() + "." + proceedingJoinPoint.getSignature().getName();
        long start = System.nanoTime();

        Object result = null;

        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception during advice execution: {}", e.getMessage());
        }

        CompletableFuture rFuture = (CompletableFuture)result;

        return rFuture.whenCompleteAsync((unused, throwable) -> {
            if (throwable != null) {
                log.error("Execution during advice execution after running  method '{}': {}", fullName, ((Throwable)throwable).getMessage());
            } else {
                long end = System.nanoTime();
                long duration = end - start;
                service.save(new MethodExecution(fullName, duration, true))
                        .subscribe(execution -> log.debug("Saved async method execution data with an ID {}", execution.getId()),
                                    exception -> log.error("Exception during saving: {}", exception.getMessage()));
            }
        });
    }
}
