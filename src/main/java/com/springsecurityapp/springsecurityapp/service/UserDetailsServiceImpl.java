package com.springsecurityapp.springsecurityapp.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.springsecurityapp.springsecurityapp.controller.dto.AuthCreateUserRequest;
import com.springsecurityapp.springsecurityapp.controller.dto.AuthLoginRequest;
import com.springsecurityapp.springsecurityapp.controller.dto.AuthResponse;
import com.springsecurityapp.springsecurityapp.persistence.entity.RoleEntity;
import com.springsecurityapp.springsecurityapp.persistence.entity.UserEntity;
import com.springsecurityapp.springsecurityapp.persistence.repository.RoleRepository;
import com.springsecurityapp.springsecurityapp.persistence.repository.UserRepository;
import com.springsecurityapp.springsecurityapp.util.JwtUtils;

import jakarta.validation.Valid;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RoleRepository repoRole;


    //metodo de details service para buscar un usuario en la base de datos
    @Override
    public UserDetails loadUserByUsername(String username) {
        //buscamos el usuarioEntity
        UserEntity userEntity = userRepository.findUserEntityByUsername(username).orElseThrow(() -> new UsernameNotFoundException("No fue encontrado"));

        //para recibir los permisos
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();
        //lo agregamos a la lista de permisos, sacandolos del usuario
        userEntity.getRoles().forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRoleEnum().name()))));

        userEntity.getRoles().stream().flatMap(role -> role.getPermissionList().stream()).forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));
        return new User(userEntity.getUsername(), userEntity.getPassword(), userEntity.isEnabled(), userEntity.isAccountNoExpired(), userEntity.isCredentialNoExpired(), userEntity.isAccountNoLocked(), authorityList);
    }

    //nos permite a hacer el login del usuario
	public AuthResponse loginUser(AuthLoginRequest authLoginRequest) {

        String username = authLoginRequest.username();
        String password = authLoginRequest.password();

        Authentication authentication = this.autenticate(username, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(username, "User loged successfully", accessToken, true);
        return authResponse;
	}

    //para autenticarnos
    private Authentication autenticate(String username, String password) {
        UserDetails userDetails = this.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Incorrect password");
        }

        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());

    }

    //para crear un usuario 
    public AuthResponse createUser(@Valid AuthCreateUserRequest authCreateUserRequest) {
        String username = authCreateUserRequest.username();
        String password = authCreateUserRequest.password();

        List<String> roleRequest = authCreateUserRequest.roleRequest().roleListName();

       Set<RoleEntity> roleEntitySet = repoRole.findRoleEntitiesByRoleEnumIn(roleRequest).stream().collect(Collectors.toSet());

        if (roleEntitySet.isEmpty()) {
            throw new IllegalArgumentException("The roles specified does not exist");
        }

        UserEntity userEntity = UserEntity.builder()
        .username(username)
        .password(passwordEncoder.encode(password))
        .roles(roleEntitySet)
        .isEnabled(true)
        .accountNoLocked(true)
        .accountNoExpired(true)
        .credentialNoExpired(true)
        .build();

        UserEntity userCreated = userRepository.save(userEntity);
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        userCreated.getRoles().forEach(r -> authorityList.add( new SimpleGrantedAuthority("ROLE_".concat(r.getRoleEnum().name()))));
                    
        userCreated.getRoles().stream()
                .flatMap(role -> role.getPermissionList().stream())
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getName())));
        //es hora de darle acceso
        SecurityContext securityContextHolder  = SecurityContextHolder.getContext();

        Authentication authentication = new UsernamePasswordAuthenticationToken(userCreated, null, authorityList);

        String accessToken = jwtUtils.createToken(authentication);

        AuthResponse authResponse = new AuthResponse(username, "User created successfully", accessToken, true);


        return authResponse;




    }






    

}
