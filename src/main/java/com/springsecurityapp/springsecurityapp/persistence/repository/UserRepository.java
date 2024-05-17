package com.springsecurityapp.springsecurityapp.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.springsecurityapp.springsecurityapp.persistence.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    Optional<UserEntity> findUserEntityByUsername(String username);

}
