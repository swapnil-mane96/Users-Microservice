package com.photoapp.users.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
	private String email;
	private String encryptedPassword;
}
