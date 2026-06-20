package com.example.AuthApp.Registry.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.AuthApp.Registry.Exceptions.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> ResourceNotFoundExceptionHandler(Exception ex){
		HashMap<String,Object > map =new HashMap<>();
		map.put("Message", ex.getMessage());
		map.put("Status",HttpStatus.NOT_FOUND);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(map);
	}
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> IllegalArgumentExceptionHandler(Exception ex){
		HashMap<String,Object > map =new HashMap<>();
		map.put("Message", ex.getMessage());
		map.put("Status",HttpStatus.BAD_REQUEST);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(map);
	}
	
}
