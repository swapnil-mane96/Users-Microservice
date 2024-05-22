package com.photoapp.users.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.photoapp.users.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Long> {
	Users findByEmail(String email);
	Users findByPublicUserId(String userId);
}
