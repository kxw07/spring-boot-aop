package com.example.springbootaop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class PostCompileAspect {
    @Pointcut(value = "execution(* com.squareup.okhttp.OkHttpClient.newCall(..))")
    public void execute() {
    }

    @Around("execute()")
    public Object getDoAround(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("execute before");
        Object response = joinPoint.proceed();
        System.out.println("execute after");

        return response;
    }
}
