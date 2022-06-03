package com.exchanger.exchange_api.aspect;

import com.exchanger.exchange_api.enumeration.ErrorCode;
import com.exchanger.exchange_api.exception.HttpResponseException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.ConstraintViolationException;
import java.lang.annotation.Annotation;

@Configuration
@ConfigurationProperties("interceptor")
@Aspect
public class ControllerAspect {
    private final Logger logger = LoggerFactory.getLogger("API CALL");

    @Pointcut("execution(* *.*(..)) && within(com.exchanger.exchange_api.controller.*)")
    public void controllers() {}

    @Pointcut("within(@org.springframework.web.bind.annotation.RequestMapping *)")
    public void requestMapping() {}

    @Around("controllers() && requestMapping()")
    public Object around(ProceedingJoinPoint joinPoint) {
        final long startTime = System.currentTimeMillis();

        try {
            Object o = joinPoint.proceed();
            //todo Request body if any
            //todo response code if any
            new Thread(() -> logRequest(joinPoint, startTime)).start();
            return o;
        } catch (Throwable e) {
            if (e instanceof HttpResponseException)
                //todo log error
                return ((HttpResponseException) e).getResponse();
            if (e instanceof ConstraintViolationException ||
                    e instanceof MethodArgumentNotValidException) {
                return new HttpResponseException(ErrorCode.VALIDATION_ERROR, e.getMessage()).getResponse();
            }
            return new HttpResponseException(ErrorCode.INTERNAL_ERROR).getResponse();
        }
    }

    private void logRequest(ProceedingJoinPoint joinPoint, final long startTime) {
        final long timeTaken = System.currentTimeMillis() - startTime;
        final String method = joinPoint.getSignature().getName();
        final String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        final String args = getArguments(joinPoint);

        logger.info("{} ms | {}#{}({})", timeTaken, className, method, args);
    }

    private String getArguments(ProceedingJoinPoint joinPoint) {
        final MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        final Object[] args = joinPoint.getArgs();
        final String[] paramNames = methodSig.getParameterNames();
        final StringBuilder arguments = new StringBuilder();
        final Annotation[][] parameterAnnotations = methodSig.getMethod().getParameterAnnotations();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (parameterAnnotations[i].length > 0 && parameterAnnotations[i][0] instanceof PathVariable)
                arguments
                        .append(paramNames[i])
                        .append("=")
                        .append(args[i])
                        .append(", ");
        }

        if (arguments.length() > 0)
            arguments.delete(arguments.length() - 2, arguments.length());

        return arguments.toString();
    }
}
