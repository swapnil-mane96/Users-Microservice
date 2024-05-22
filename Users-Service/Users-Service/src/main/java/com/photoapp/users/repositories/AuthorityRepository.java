package com.photoapp.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.photoapp.users.entity.AuthorityEntity;

public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Long> {

	AuthorityEntity findByAuthorityName(String authorityName);
}
