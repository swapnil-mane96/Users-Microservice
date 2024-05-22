package com.photoapp.users.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewUserResponse {
	private String publicUserId;
	private String firstName;
	private String lastName;
	private String email;
}
