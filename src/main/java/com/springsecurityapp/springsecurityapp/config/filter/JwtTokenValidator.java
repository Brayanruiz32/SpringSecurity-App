package com.springsecurityapp.springsecurityapp.config.filter;

import java.io.IOException;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.springsecurityapp.springsecurityapp.util.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtTokenValidator extends OncePerRequestFilter{

    //creamos la variable jwtutils
    private JwtUtils jwtUtils;

    //la inicializamos en el constructor 
    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwtToken != null) {
            
            jwtToken = jwtToken.substring(7);
            
            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);
            
            String username = jwtUtils.extractUserName(decodedJWT);
            
            String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT, "authorities").asString();

            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);
            //creamos un contexto nuevo 
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            //creamos un objeto nuevo de username and password authentication
            Authentication authenticationToken  = new UsernamePasswordAuthenticationToken(username, null, authorities);
            //seteamos en el contexto 
            context.setAuthentication(authenticationToken);

            SecurityContextHolder.setContext(context);
            System.out.println("estoy dentro del JwtTokenValidator");
        }

        filterChain.doFilter(request, response);  
    }

    

}
