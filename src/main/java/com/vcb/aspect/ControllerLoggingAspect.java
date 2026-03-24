package com.vcb.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class ControllerLoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointcut() {}

    @Around("restControllerPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes()).getRequest();

        String controllerName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName     = joinPoint.getSignature().getName();
        String paramLog       = buildParamLog(joinPoint);

        log.info("[API] --> {} {} | {}.{}(){}",
                request.getMethod(), request.getRequestURI(), controllerName, methodName, paramLog);

        long start = System.currentTimeMillis();
        try {
            Object result  = joinPoint.proceed();
            long   elapsed = System.currentTimeMillis() - start;
            String status  = (result instanceof ResponseEntity<?> re)
                    ? re.getStatusCode().toString() : "OK";
            log.info("[API] <-- {} | {}ms", status, elapsed);
            return result;
        } catch (Exception e) {
            log.error("[API] <-- ERROR {} | {}ms | {}",
                    e.getClass().getSimpleName(), System.currentTimeMillis() - start, e.getMessage());
            throw e;
        }
    }

    private String buildParamLog(ProceedingJoinPoint joinPoint) {
        Parameter[] parameters = ((MethodSignature) joinPoint.getSignature()).getMethod().getParameters();
        Object[]    args       = joinPoint.getArgs();
        StringBuilder sb       = new StringBuilder();

        for (int i = 0; i < parameters.length; i++) {
            appendParamEntry(sb, parameters[i], args[i]);
        }
        return sb.toString();
    }

    private void appendParamEntry(StringBuilder sb, Parameter param, Object arg) {
        if (arg instanceof Jwt || arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
            return;
        }
        if (param.isAnnotationPresent(PathVariable.class)) {
            String name = resolveParamName(param.getAnnotation(PathVariable.class).value(), param.getName());
            sb.append(String.format("%n  @PathVariable [%s] = %s", name, arg));
        } else if (param.isAnnotationPresent(RequestParam.class)) {
            String name = resolveParamName(param.getAnnotation(RequestParam.class).value(), param.getName());
            sb.append(String.format("%n  @RequestParam  [%s] = %s", name, arg));
        } else if (param.isAnnotationPresent(RequestBody.class)) {
            sb.append(String.format("%n  @RequestBody   = %s", arg));
        }
    }

    private String resolveParamName(String annotationValue, String fallback) {
        return annotationValue.isBlank() ? fallback : annotationValue;
    }
}
