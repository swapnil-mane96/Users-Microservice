package com.photoapp.users.feignclient;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.photoapp.users.model.AlbumResponseModel;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@FeignClient(name = "albums-ws")
public interface AlbumsServiceClient {
	
	@GetMapping("/users/{id}/albums")
	@Retry(name = "albums-ws")
	@CircuitBreaker(name = "albums-ws", fallbackMethod = "getAlbumsFallback")
	List<AlbumResponseModel> getAlbums(@PathVariable String id, @RequestHeader("Authorization") String authorization);
	
	/*
	 * fallbackMethod should be declared in same interface
	 * name should be same as declared in calling fallbackMethod
	 * method return type must be same as method name which used @CircuitBreaker to call fallbackMethod
	 */
	default List<AlbumResponseModel> getAlbumsFallback(String id, String authorization, Throwable exception){
		System.out.println("Param "+id);
		System.out.println("Exception took place "+exception.getMessage());
		return new ArrayList<>();
	}

}
