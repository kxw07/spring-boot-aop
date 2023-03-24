//package com.example.springbootaop.rest;
//
//public aspect CustomRestTemplateAspect {
//
//    pointcut callExchange(): within(CustomRestTemplateAspect) && execution(* *(..));
//
//    before(): callExchange() {
//        System.out.println("CustomRestTemplateAspect before");
//    }
//
//    after(): callExchange() {
//        System.out.println("CustomRestTemplateAspect after");
//    }
//}
