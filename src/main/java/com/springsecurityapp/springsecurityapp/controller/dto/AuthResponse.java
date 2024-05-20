package com.springsecurityapp.springsecurityapp.controller.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;


//seteamos un orden para mostrar las propiedades 
@JsonPropertyOrder({"username", "message", "jwt", "status"})
public record AuthResponse(
    String username,
    String message, 
    String jwt, 
    boolean status
) {

}
