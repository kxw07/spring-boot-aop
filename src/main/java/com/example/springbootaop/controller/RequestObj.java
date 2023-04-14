package com.example.springbootaop.controller;

import lombok.Builder;
import lombok.Value;

//@Builder
public class RequestObj {
    private String name;
    private Integer age;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RequestObj(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
