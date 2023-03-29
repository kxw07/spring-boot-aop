//package com.example.springbootaop.config;
//
//
//public aspect RestTemplateAspect {
//
//    pointcut callExchange(): execution(* org.springframework.web.client.RestTemplate.exchange(..));
//
//    before(): callExchange() {
//        System.out.println("RestTemplateAspect before");
//    }
//
//    after(): callExchange() {
//        System.out.println("RestTemplateAspect after");
//    }
//}
//
