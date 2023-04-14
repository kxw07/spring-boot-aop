package com.example.springbootaop.controller;


import com.example.springbootaop.rest.CustomRestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    private final CustomRestTemplate customRestTemplate;

    public MainController(CustomRestTemplate customRestTemplate) {
        this.customRestTemplate = customRestTemplate;
    }

    @GetMapping(value = "get", produces = "application/json")
    public String get() throws JsonProcessingException {
        final RequestObj requestObj = new RequestObj("jack", 20);
//        final RequestObj requestObj = RequestObj.builder().name("jack").age(20).build();

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(null);

        return customRestTemplate.exchange("http://127.0.0.1:8080/root", HttpMethod.POST, requestObj, String.class);
    }

    @PostMapping(value = "root", produces = "application/json")
    public String root(String name) {
        return "root";
    }
}
