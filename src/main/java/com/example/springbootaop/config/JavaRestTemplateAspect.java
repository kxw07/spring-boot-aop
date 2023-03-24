package com.example.springbootaop.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class JavaRestTemplateAspect {
    @Pointcut(value = "execution(* org.springframework.web.client.RestTemplate.exchange(..))")
    public void exchange() {
    }

    @Around("exchange()")
    public Object getDoAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("JavaRestTemplateAspect before");

        Object response = joinPoint.proceed();
        System.out.println("JavaRestTemplateAspect after");

        return response;
    }
}
