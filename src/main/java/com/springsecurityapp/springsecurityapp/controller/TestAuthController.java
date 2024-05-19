package com.springsecurityapp.springsecurityapp.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/method")
public class TestAuthController {

    @GetMapping("/get")
    public String helloGet(){
        return "Hello world - GET";
    }

    @PostMapping("/post")
    public String helloPost(){
        return "Hello world - POST";
    }

    @PutMapping("/put")
    public String helloPut(){
        return "Hello world - PUT";
    }

    @DeleteMapping("/delete")
    public String helloDelete(){
        return "Hello world - DELETE";
    }

    @PatchMapping("/patch")
    public String helloPatch(){
        return "Hello world - PATCH";
    }


}
