package com.paulorgnascimento.cleanarchitecture.infrastructure.config;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TracingAspect {

    private final Tracer tracer;

    @Autowired
    public TracingAspect(Tracer tracer) {
        this.tracer = tracer;
    }

    @Around("execution(* com.paulorgnascimento.cleanarchitecture..*.*(..)) && !execution(* com.paulorgnascimento.cleanarchitecture.infrastructure.config.TracerConfig.*(..))") // ! execution é para evitar dependência circular
    public Object traceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Span span = tracer.spanBuilder(methodName).startSpan();
        try (Scope scope = span.makeCurrent()) {
            return joinPoint.proceed();
        } finally {
            span.end();
        }
    }
}

