package com.example.springbootaop.rest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class JavaCustomRestTemplateAspect {
    @Pointcut(value = "execution(* com.example.springbootaop.rest.CustomRestTemplate.exchange(..))")
    public void exchange() {
    }

    @Around("exchange()")
    public Object getDoAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("JavaCustomRestTemplateAspect before");

        Object response = joinPoint.proceed();
        System.out.println("JavaCustomRestTemplateAspect after");

        return response;
    }
}
