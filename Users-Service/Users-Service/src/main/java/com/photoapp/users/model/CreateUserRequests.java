package com.photoapp.users.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserRequests {
	private Integer id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String publicUserId;
	private String encryptedPassword;
}
