package com.springsecurityapp.springsecurityapp.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

@Component
public class JwtUtils {
    // para nuestro caso tenemos el key private almacenado dentro del programa
    @Value("${security.jwt.key.private}")
    private String privateKey;
    // tenemos el usuario a generar
    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    // necesitamos el metodo para crear el token
    public String createToken(Authentication authentication) {
        // creamos el algoritmo de encriptacion
        Algorithm algorithm = Algorithm.HMAC256(privateKey);
        // obtenemose el principal del context holder almacenado
        String username = authentication.getPrincipal().toString();
        // obtenemos el authority almacenadas en el context holder
        String authorities = authentication.getAuthorities()
                .stream().map(g -> g.getAuthority()).collect(Collectors.joining(", "));
        // creamos el token con ayuda de la documentación
        String token = JWT.create()
                .withIssuer(this.userGenerator)// identificador de mi aplicacion que va generar el token
                .withSubject(username)// el sujeto a quien se le ha emitido el token
                .withClaim("authorities", authorities)// pasamos los permisos
                .withIssuedAt(new Date())// la creacion del token
                .withExpiresAt(new Date(System.currentTimeMillis() + 1800000))// la expiracion del token
                .withJWTId(UUID.randomUUID().toString())// la creacion del token's id aleatorio
                .withNotBefore(new Date(System.currentTimeMillis()))// partida de la validez del token
                .sign(algorithm);// y firmamos el algoritmo de encriptacion
        return token;
    }


    public DecodedJWT validateToken(String token) {
        try {
            //algoritmo de encriptacion
            Algorithm algorithm = Algorithm.HMAC256(privateKey);
            //el verificador establece el creador del token
            JWTVerifier verifier = JWT.require(algorithm)
                    //especificamos el usuario creador del token 
                    .withIssuer(userGenerator)
                    .build();
            //y utilizamos el metodo verify del objeto verifier
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT;
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token invalid, not authorized");
        }
    }

    //extraemos el nombre del usuario 
    public String extractUserName(DecodedJWT decodedJWT){
        return decodedJWT.getSubject().toString();
    }
    //extraemos un claim especifico 
    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }
    //extraemos todos los claims posibles
    public Map<String, Claim> getAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }


}
