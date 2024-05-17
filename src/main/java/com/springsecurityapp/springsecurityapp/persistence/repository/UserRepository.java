package com.springsecurityapp.springsecurityapp.persistence.repository;

import java.util.List;

import org.apache.catalina.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.springsecurityapp.springsecurityapp.persistence.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Long> {

    

}
