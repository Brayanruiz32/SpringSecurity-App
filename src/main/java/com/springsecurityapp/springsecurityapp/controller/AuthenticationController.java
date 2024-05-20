package com.springsecurityapp.springsecurityapp.controller;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springsecurityapp.springsecurityapp.controller.dto.AuthLoginRequest;
import com.springsecurityapp.springsecurityapp.controller.dto.AuthResponse;
import com.springsecurityapp.springsecurityapp.service.UserDetailsServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private UserDetailsServiceImpl userDetailService;


    //metodo para crear un usuario
    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(AuthCreateUser authCreateUser){
        return new ResponseEntity<>(userDetailService.createUser(), HttpStatus.CREATED);
    }


    //metodo para iniciar sesion 
    @PostMapping("/log-in")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthLoginRequest userRequest){

        return new ResponseEntity<>(userDetailService.loginUser(userRequest), HttpStatus.OK);

    }



    


}
