package com.photoapp.users.serviceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.photoapp.users.dtos.UsersDto;
import com.photoapp.users.entity.AuthorityEntity;
import com.photoapp.users.entity.RoleEntity;
import com.photoapp.users.entity.Users;
import com.photoapp.users.enums.Roles;
import com.photoapp.users.exception.UserNotFoundException;
import com.photoapp.users.feignclient.AlbumsServiceClient;
import com.photoapp.users.model.AlbumResponseModel;
import com.photoapp.users.repositories.AuthorityRepository;
import com.photoapp.users.repositories.RolesRepository;
import com.photoapp.users.repositories.UsersRepository;
import com.photoapp.users.service.UsersService;

import feign.FeignException;
import jakarta.transaction.Transactional;
@Service
public class UsersServiceImpl implements UsersService {
	
	Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);
	
	UsersRepository usersRepository;
	//PasswordEncoder passwordEncoder;
	BCryptPasswordEncoder bCryptPasswordEncoder;
	//RestTemplate restTemplate;
	Environment environment;
	AlbumsServiceClient albumsServiceClient;
	
	@Autowired
	AuthorityRepository authorityRepository;
	
	@Autowired
	RolesRepository rolesRepository;
	
	public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder passwordEncoder, 
			AlbumsServiceClient albumsServiceClient, Environment environment) {
		this.usersRepository = usersRepository;
		this.bCryptPasswordEncoder = passwordEncoder;
		this.albumsServiceClient = albumsServiceClient;
		this.environment = environment;
	}
	
	//@Autowired
//	public UsersServiceImpl(UsersRepository usersRepository, BCryptPasswordEncoder passwordEncoder, 
//			RestTemplate restTemplate, Environment environment) {
//		this.usersRepository = usersRepository;
//		this.bCryptPasswordEncoder = passwordEncoder;
//		this.restTemplate = restTemplate;
//		this.environment = environment;
//	}
	
	@Override
	public UsersDto createUsers(UsersDto usersDto) {
		usersDto.setPublicUserId(UUID.randomUUID().toString());
		usersDto.setEncryptedPassword(this.bCryptPasswordEncoder.encode(usersDto.getEncryptedPassword()));
		
		AuthorityEntity readAuthority = createAuthority("READ");
		AuthorityEntity writeAuthority = createAuthority("WRITE");
		AuthorityEntity deleteAuthority = createAuthority("DELETE");
		
		RoleEntity roleUser = createRole(Roles.ROLE_USER.name(), Arrays.asList(readAuthority, writeAuthority, deleteAuthority));
		if (roleUser == null) {
			return null;
		}
		
		usersDto.setRoles(Arrays.asList(roleUser));
		
		ModelMapper modelMapper = new ModelMapper();
		//Used to match fields between DTO and Entity Strictly
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT); 
		Users users = modelMapper.map(usersDto, Users.class);
		//users.setEncryptedPassword("test");
		this.usersRepository.save(users);
		UsersDto result = modelMapper.map(users, UsersDto.class);
		return result;
	}
	
	@Transactional
	private AuthorityEntity createAuthority(String authorityName) {
		AuthorityEntity authorityEntity = this.authorityRepository.findByAuthorityName(authorityName);
		if (authorityEntity == null) {
			authorityEntity = new AuthorityEntity(authorityName);
			this.authorityRepository.save(authorityEntity);
		}
		return authorityEntity;
	}
	
	@Transactional
	private RoleEntity createRole(String roleName, Collection<AuthorityEntity> authorities) {
		RoleEntity roleEntity = this.rolesRepository.findByRoleName(roleName);
		if(roleEntity == null) {
			roleEntity = new RoleEntity(roleName, authorities);
			this.rolesRepository.save(roleEntity);
		}
		return roleEntity;
	}

	/*
	 * The responsibility of this method is to find user in a database by using their username and return back
	   to spring framework user object with user credentials, including user's encrypted password, user roles and authorities.
	   So in this method, we use find by email query method to reduce the details from a database.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users users = this.usersRepository.findByEmail(username);
		if (users == null)
			throw new UsernameNotFoundException(username);
		
		Collection<GrantedAuthority> authorities = new ArrayList<>(); //The user class accepts roles and authorities as a collection of granted authority objects.
		Collection<RoleEntity> roles = users.getRoles();
		roles.forEach((role) -> {
			authorities.add(new SimpleGrantedAuthority(role.getRoleName())); // gives list of roles
			Collection<AuthorityEntity> authorityEntities = role.getAuthorities();
			authorityEntities.forEach((authorityEntity) -> {
				authorities.add(new SimpleGrantedAuthority(authorityEntity.getAuthorityName()));
			});
		});
		return new User(users.getEmail(), 
				users.getEncryptedPassword(),
				true, true, true,
				true, authorities);
	}

	@Override
	public UsersDto getUserDetailsByEmail(String email) {
		Users users = this.usersRepository.findByEmail(email);
		if (users == null)
			throw new UsernameNotFoundException(email);
		return new ModelMapper().map(users, UsersDto.class);
	}

	@Override
	public UsersDto getUserById(String userId, String authorization) {
		ModelMapper modelMapper = new ModelMapper();
		Users userEntity = this.usersRepository.findByPublicUserId(userId);
		if (userEntity == null) {
			throw new UserNotFoundException("User not found pls register first!!");
		}
		UsersDto usersDto = modelMapper.map(userEntity, UsersDto.class);
		
//		String albumsUrl = String.format(this.environment.getProperty("albums.url"), userId);
//		
//		ResponseEntity<List<AlbumResponseModel>> albumListResponse = this.restTemplate.exchange(albumsUrl,
//				HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
//		});
//		
//		//To extract list of album response models from ResponseEntity, we are using getBody()
//		List<AlbumResponseModel> albumsList = albumListResponse.getBody();
		
		List<AlbumResponseModel> albumsList = null;
		this.logger.debug("Before calling Album-service...");
		try {
			albumsList = this.albumsServiceClient.getAlbums(userId, authorization);
			this.logger.debug("After calling Album-service...");
		} catch (FeignException e) {
			this.logger.debug("Error occured "+e.getMessage());
			//e.printStackTrace();
		}
		usersDto.setAlbums(albumsList);
		return usersDto;
	}

}
