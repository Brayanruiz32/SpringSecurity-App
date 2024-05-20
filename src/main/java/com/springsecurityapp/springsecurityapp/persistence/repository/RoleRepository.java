package com.springsecurityapp.springsecurityapp.persistence.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.springsecurityapp.springsecurityapp.persistence.entity.RoleEntity;

public interface RoleRepository extends CrudRepository<RoleEntity, Long> {

    List<RoleEntity> findRoleEntitiesByRoleEnumIn(List<String> roleNames);


}
