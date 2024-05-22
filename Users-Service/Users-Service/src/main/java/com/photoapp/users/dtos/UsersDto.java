package com.photoapp.users.dtos;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.photoapp.users.entity.RoleEntity;
import com.photoapp.users.model.AlbumResponseModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersDto implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4517084165123316804L;
	private Integer id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String publicUserId;
	private String encryptedPassword;
	private List<AlbumResponseModel> albums;
	private Collection<RoleEntity> roles;
}
