package com.exchanger.exchange_api.aspect;

import com.exchanger.exchange_api.dto.ErrorResponseDTO;
import com.exchanger.exchange_api.enumeration.ErrorCode;
import com.exchanger.exchange_api.exception.HttpResponseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.ConstraintViolationException;
import java.lang.annotation.Annotation;

@Configuration
@ConfigurationProperties("interceptor")
@Aspect
public class ControllerAspect {
    private final Logger logger = LoggerFactory.getLogger("API CALL");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* *.*(..)) && within(com.exchanger.exchange_api.controller.*)")
    public void controllers() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RequestMapping *)")
    public void requestMapping() {
    }

    @Around("controllers() && requestMapping()")
    public Object around(ProceedingJoinPoint joinPoint) {
        final long startTime = System.currentTimeMillis();

        try {
            Object o = joinPoint.proceed();
            new Thread(() -> logRequest(joinPoint, startTime, (ResponseEntity<?>) o)).start();
            return o;
        } catch (Throwable e) {
            ResponseEntity<ErrorResponseDTO> errorResponse;
            if (e instanceof HttpResponseException)
                errorResponse = ((HttpResponseException) e).getResponse();
            else if (e instanceof ConstraintViolationException ||
                    e instanceof MethodArgumentNotValidException) {
                errorResponse = new HttpResponseException(ErrorCode.VALIDATION_ERROR, e.getMessage()).getResponse();
            } else {
                e.printStackTrace();
                errorResponse = new HttpResponseException(ErrorCode.INTERNAL_ERROR).getResponse();
            }

            new Thread(() -> logRequest(joinPoint, startTime, errorResponse)).start();
            return errorResponse;
        }
    }

    private void logRequest(ProceedingJoinPoint joinPoint, final long startTime, ResponseEntity<?> response) {
        final long timeTaken = System.currentTimeMillis() - startTime;
        final String method = joinPoint.getSignature().getName();
        final String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        final String args = getArguments(joinPoint);
        final int statusCode = response.getStatusCodeValue();
        final Object requestBody = getRequestBody(joinPoint);

        if (requestBody != null) logBody(requestBody, "Request");
        logger.info("{} | {}#{}({}) | {} ms ", statusCode, className, method, args, timeTaken);
        if (statusCode != 204) logBody(response.getBody(), "Response");
    }

    private void logBody(Object request, String type) {
        try {
            final String body = objectMapper.writeValueAsString(request);
            logger.info("{} Body: {}", type, body);
        } catch (JsonProcessingException ignored) {
        }
    }

    private String getArguments(ProceedingJoinPoint joinPoint) {
        final MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        final Object[] args = joinPoint.getArgs();
        final String[] paramNames = methodSig.getParameterNames();
        final StringBuilder arguments = new StringBuilder();
        final Annotation[][] parameterAnnotations = methodSig.getMethod().getParameterAnnotations();
        final StringBuilder queryParams = new StringBuilder("?");

        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (parameterAnnotations[i].length == 0) continue;
            if (parameterAnnotations[i][0] instanceof PathVariable)
                arguments
                        .append(paramNames[i])
                        .append("=")
                        .append(args[i])
                        .append(", ");
            else if (parameterAnnotations[i][0] instanceof RequestParam)
                queryParams
                        .append(paramNames[i])
                        .append("=")
                        .append(args[i])
                        .append("&");
        }

        if (arguments.length() > 0)
            arguments.delete(arguments.length() - 2, arguments.length());

        arguments.append(queryParams.delete(queryParams.length() - 1, queryParams.length()));

        return arguments.toString();
    }

    private Object getRequestBody(ProceedingJoinPoint joinPoint) {
        final MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        if (!methodSig.getMethod().isAnnotationPresent(PostMapping.class)) return null;

        final Annotation[][] parameterAnnotations = methodSig.getMethod().getParameterAnnotations();
        final Object[] args = joinPoint.getArgs();

        for (int i = 0; i < parameterAnnotations.length; i++) {
            if (parameterAnnotations[i].length > 0 && parameterAnnotations[i][0] instanceof RequestBody) {
                return args[i];
            }
        }
        return null;
    }
}
