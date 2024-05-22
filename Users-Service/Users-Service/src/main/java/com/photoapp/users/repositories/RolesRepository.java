package com.photoapp.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.photoapp.users.entity.RoleEntity;

public interface RolesRepository extends JpaRepository<RoleEntity, Long> {

	RoleEntity findByRoleName(String roleName);
}
