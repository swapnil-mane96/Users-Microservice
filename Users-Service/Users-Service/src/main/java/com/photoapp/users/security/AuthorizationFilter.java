package com.photoapp.users.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.SecretKey;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.photoapp.jwtauthorities.JwtParserClaims;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * this class to work as a AuthorizationFilter it extends BasicAuthenticationFilter
 */
public class AuthorizationFilter extends BasicAuthenticationFilter {

	private Environment environment;
	
	public AuthorizationFilter(AuthenticationManager authenticationManager, Environment environment) {
		super(authenticationManager);
		this.environment=environment;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
				throws IOException, ServletException {
			//super.doFilterInternal(request, response, chain);
		String authorizationHeader = request.getHeader(this.environment.getProperty("authorization.token.header.name"));
			if (authorizationHeader == null || !authorizationHeader.startsWith(this.environment.getProperty("authorization.token.header.prefix"))) {
				chain.doFilter(request, response);
				return;
			}
			UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
			// used to access method level security
			SecurityContextHolder.getContext().setAuthentication(authentication);
			chain.doFilter(request, response);
		}

		private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

			String authorizationHeader = request.getHeader(this.environment.getProperty("authorization.token.header.name"));

			if (authorizationHeader == null) {
				return null;
			}

			String token = authorizationHeader.replace(this.environment.getProperty("authorization.token.header.prefix"), "").trim();
			String tokenSecret = this.environment.getProperty("token.secret");

			if (tokenSecret == null) {
				return null;
			}

			JwtParserClaims jwtParserClaims = new JwtParserClaims(token, tokenSecret);
			String userId = jwtParserClaims.getJwtSubject();
			
			
			
			if (userId == null) {
				return null;
			}

			return new UsernamePasswordAuthenticationToken(userId, null, jwtParserClaims.getUserAuthorities());

		}

}
