package com.photoapp.users.controller;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.photoapp.users.dtos.UsersDto;
import com.photoapp.users.model.CreateUserRequests;
import com.photoapp.users.model.NewUserResponse;
import com.photoapp.users.model.UserResponseModel;
import com.photoapp.users.service.UsersService;

@RestController
@RequestMapping("/users")
public class UsersController {
	
	@Autowired
	private Environment environment; //used to see port number of multiple instances of a service
	
	@Autowired
	private UsersService usersService;
	
	@GetMapping("/status/check")
	public String status() {
		return "Users Microservice is Working Fine... "+environment.getProperty("local.server.port")
		+", with token = "+ this.environment.getProperty("token.secret");
	}
	
	@PostMapping(path = "/createusers", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<NewUserResponse> createUsers(@RequestBody CreateUserRequests createUserRequests){
		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		UsersDto usersDto = modelMapper.map(createUserRequests, UsersDto.class);
		
		UsersDto registeredUser = this.usersService.createUsers(usersDto);
		
		NewUserResponse newUserResponse = modelMapper.map(registeredUser, NewUserResponse.class);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(newUserResponse);
		
	}
	
	@GetMapping(path = "/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE})
	//@PreAuthorize("principal == #userId")
	//@PostAuthorize("principal == returnObject.body.publicUserId")
	@PreAuthorize("hasRole('ADMIN') or principal == #userId")
	//@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponseModel> getUser(@PathVariable String userId,
			@RequestHeader("Authorization") String authorization){
		ModelMapper modelMapper = new ModelMapper();
		
		UsersDto usersDto = this.usersService.getUserById(userId, authorization);
		
		UserResponseModel userResponseModel = modelMapper.map(usersDto, UserResponseModel.class);
		
		return ResponseEntity.status(HttpStatus.OK).body(userResponseModel);
	}
	
	@PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFILE_DELETE') or principal == #userId")
	//@PreAuthorize("hasRole('ADMIN') or hasAuthority('PROFILE_DELETE')")
	@DeleteMapping(path = "/{userId}", produces = {MediaType.APPLICATION_JSON_VALUE})
	public String deleteUser(@PathVariable String userId) {
		return "Deleting user "+userId;
	}
	
	
	
}
