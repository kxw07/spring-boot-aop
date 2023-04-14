package com.example.springbootaop.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class RestTemplateAspect {
    @Pointcut(value = "execution(* org.springframework.web.client.RestTemplate.exchange(..))")
    public void exchange() {
    }

    @Around("exchange()")
    public Object getDoAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("RestTemplateAspect before");
        Object response = joinPoint.proceed();
        System.out.println("RestTemplateAspect after");

        return response;
    }
}
