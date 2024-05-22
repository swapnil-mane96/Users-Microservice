package com.photoapp.users.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.photoapp.users.service.UsersService;

@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	private Environment environment;
	private UsersService usersService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public WebSecurityConfig(Environment environment, UsersService usersService,
			BCryptPasswordEncoder bCryptPasswordEncoder ) {
		this.environment = environment;
		this.usersService = usersService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

	@Bean
    SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
		 	// configure authentication manager builder
		 	AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
		 	authenticationManagerBuilder.userDetailsService(this.usersService).passwordEncoder(this.bCryptPasswordEncoder);
		 	AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
		 	
		 	// create Authentication filter
		 	AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager,usersService, environment);
		 	authenticationFilter.setFilterProcessesUrl(this.environment.getProperty("login.url.path"));
		 	
			httpSecurity.csrf((csrf) -> csrf.disable());
			httpSecurity
			.authorizeHttpRequests((auth) -> auth.requestMatchers("/users/createusers", "/h2-console/**", "/users/status/check", "/actuator/**", "/users/**")
			.permitAll())
			.addFilter(new AuthorizationFilter(authenticationManager, environment))
			.addFilter(authenticationFilter)
			.authenticationManager(authenticationManager)
			.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
			httpSecurity.headers((headers) -> headers.frameOptions((fmo) -> fmo.disable()));	
			return httpSecurity.build();
		}
	
	
	
	
//    @Bean
//    SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
//		 	// configure authentication manager builder
//		 	AuthenticationManagerBuilder authenticationManagerBuilder = httpSecurity.getSharedObject(AuthenticationManagerBuilder.class);
//		 	authenticationManagerBuilder.userDetailsService(this.usersService).passwordEncoder(this.bCryptPasswordEncoder);
//		 	AuthenticationManager authenticationManager = authenticationManagerBuilder.build();
//		 	
//		 	// create Authentication filter
//		 	AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager,usersService, environment);
//		 	authenticationFilter.setFilterProcessesUrl(this.environment.getProperty("login.url.path"));
//		 	
//			httpSecurity.csrf(csrf -> csrf.disable())
//			.authorizeHttpRequests(auth -> auth.requestMatchers("/users/createusers", "/h2-console/**", "/users/status/check", "/actuator/**", "/users/**")
//			.permitAll()
//			.anyRequest().authenticated()
//			.and()
//			.addFilter(authenticationFilter)
//			.authenticationManager(authenticationManager))
//			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//			httpSecurity.headers(headers -> headers.frameOptions(fmo -> fmo.disable()));	
//			return httpSecurity.build();
//		}
    
}
