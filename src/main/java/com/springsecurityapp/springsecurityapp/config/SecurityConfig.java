package com.springsecurityapp.springsecurityapp.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.springsecurityapp.springsecurityapp.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
// me permite trabajar con springboot security mediante anotaciones
@EnableMethodSecurity
public class SecurityConfig {

    // el componente que pasa por 12 filtros la autenticacion del usuario (1)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // NOTA: cuando trabajamos con rest, no necesitamos la proteccion csrf, pero si
                // es con formularios, fijo que si
                .csrf(csrf -> csrf.disable())
                // NOTA: se utiliza cuando el formulario te pide usuario y contraseña
                .httpBasic(Customizer.withDefaults())
                // NOTA: debes crear una policy para que sea un STATELESS (no guarda datos en la
                // BD)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // NOTA: se autoriza el metodo y luego la URL
                .authorizeHttpRequests(http -> {
                    // configurar los endpoints publicos
                    http.requestMatchers(HttpMethod.GET, "/auth/get").permitAll();
                    // configurar los endpoints privados
                    http.requestMatchers(HttpMethod.POST, "/auth/post").hasAnyRole("ADMIN", "DEVELOPER");
                    //http.requestMatchers(HttpMethod.POST, "/auth/post").hasAnyAuthority("CREATE", "READ");
                    //patch
                    http.requestMatchers(HttpMethod.PATCH, "/auth/patch").hasAnyAuthority("REFACTOR");
                    // configurar los endpoints no especificados
                    http.anyRequest().denyAll();
                    // http.anyRequest().authenticated();
                })
                // NOTA: por el patron de construccion builder
                .build();
    }

    // el manejador de la autenticacion, super importante para poder hacer el manejo
    // (2)
    // de la data del usuario que conectará con la base de datos
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // componente llamado por el authentication manager, importante para la conexion
    // (3)
    // con la BD
    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsServiceImpl userDetailsService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());// el password
        provider.setUserDetailsService(userDetailsService);// el user
        System.out.println(provider);
        return provider;
    }

    // una simple creacion de un usuario en memoria para no tener la necesidad de
    // extraerla de una BD

    // componente para decodificar la contraseña, super imporntate para hacer un
    // managament de la constraseña
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
