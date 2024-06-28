package com.amalitech.surveysphere.controllers;

import org.springframework.web.bind.annotation.RequestMapping;

public class HelloWorldControllerTest {

    @RequestMapping("/")
    public String getPlanet(){
        return "Hello, Welcome to Earth";
    }

}
