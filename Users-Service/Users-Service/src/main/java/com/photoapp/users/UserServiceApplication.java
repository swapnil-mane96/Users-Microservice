package com.photoapp.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

import feign.Logger;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class UserServiceApplication {

	@Autowired
	Environment environment;
	
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

//	@Bean
//	PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
	
	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	HttpExchangeRepository httpExchangeRepository() {
		return new InMemoryHttpExchangeRepository();
	}
	
	/*
	 * @LoadBalanced:This is to enable client-side load balancing for rest template.
	 */
	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean
	Logger.Level feignLoggerLevel(){
		return Logger.Level.FULL;
	}

	@Bean
	@Profile("production")
	Logger.Level feignProductionLoggerLevel(){
		return Logger.Level.BASIC;
	}

    @Bean
    @Profile("production")
    String createProductionBean() {
		System.out.println("In production env bean: "+this.environment.getProperty("myapplication.environment"));
		return "Production Bean";
	}
    
    @Bean
    @Profile("!production")
    String createNotForProductionBean() {
		System.out.println("In not production env bean: "+this.environment.getProperty("myapplication.environment"));
		return "Not for production Bean";
	}
    
    @Bean
    @Profile("default")
    String createDefaultBean() {
		System.out.println("In default env bean: "+this.environment.getProperty("myapplication.environment"));
		return "Default Bean";
	}
}
