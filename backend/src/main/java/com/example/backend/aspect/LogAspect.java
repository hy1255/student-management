package com.example.backend.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;   // ← 关键修改：javax → jakarta

import java.util.Arrays;

@Aspect
@Component
public class LogAspect {

    @Pointcut("execution(* com.example.backend.controller..*.*(..))")
    public void controllerLog() {}

    @Before("controllerLog()")
    public void doBefore(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) return;
        HttpServletRequest request = attributes.getRequest();

        System.out.println("========== 请求开始 ==========");
        System.out.println("URL: " + request.getRequestURL().toString());
        System.out.println("Method: " + request.getMethod());
        System.out.println("IP: " + request.getRemoteAddr());
        System.out.println("Class Method: " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        System.out.println("Parameters: " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "result", pointcut = "controllerLog()")
    public void doAfterReturning(Object result) {
        System.out.println("Response: " + JSON.toJSONString(result));
        System.out.println("========== 请求结束 ==========");
    }
}