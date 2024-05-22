package com.photoapp.users.security;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.photoapp.users.dtos.UsersDto;
import com.photoapp.users.model.LoginRequest;
import com.photoapp.users.service.UsersService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private UsersService usersService;
	private Environment environment;
	
	public AuthenticationFilter(AuthenticationManager authenticationManager, UsersService usersService,
			Environment environment) {
		super(authenticationManager);
		this.usersService = usersService;
		this.environment = environment;
	}
	
	// When user enters username and password then for authentication purpose this method will call
	@Override
	public Authentication attemptAuthentication(HttpServletRequest httpServletRequest,
			HttpServletResponse attemptAuthentication) throws AuthenticationException {
		try {
			LoginRequest loginRequest = new ObjectMapper().readValue(httpServletRequest.getInputStream(),
					LoginRequest.class); //after this line LoginRequest model has username and password

			return getAuthenticationManager().authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
							loginRequest.getEncryptedPassword(),
							new ArrayList<>()));
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}
	
	// This method will call once user is successfully authenticated using correct username and password
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain, Authentication authResult) throws IOException, ServletException {
		
		String userName = ((User)authResult.getPrincipal()).getUsername();
		UsersDto usersDto = this.usersService.getUserDetailsByEmail(userName);
		
		String tokenSecret = this.environment.getProperty("token.secret");
		System.out.println("User Service Secret Token: "+tokenSecret);
		// encode token using Base64
		byte[] secretKeyBytes = Base64.getEncoder().encode(tokenSecret.getBytes());
		// use byte array to create a sign in key
		SecretKey secretKey = Keys.hmacShaKeyFor(secretKeyBytes);
		
		Instant now = Instant.now(); // A date after which jwt token will expire
		
		String token = Jwts.builder()
				.claim("scope", authResult.getAuthorities()) //it will return a list of users authorities and they will be added to our Json web token as a scope claim.
				.subject(usersDto.getPublicUserId())
				.expiration(Date.from(now.plusMillis(Long.parseLong(this.environment.getProperty("token.expiration_time")))))
				.issuedAt(Date.from(now))
				.signWith(secretKey)
				.compact(); // call compact(); method to generate jwt token
		
		response.addHeader("token", token);
		response.addHeader("userId", usersDto.getPublicUserId());
	}

	
}
