package com.photoapp.users.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.photoapp.users.dtos.UsersDto;

public interface UsersService extends UserDetailsService {
	
	UsersDto createUsers(UsersDto usersDto);
	UsersDto getUserDetailsByEmail(String email);
	UsersDto getUserById(String userId, String authorization);
}
