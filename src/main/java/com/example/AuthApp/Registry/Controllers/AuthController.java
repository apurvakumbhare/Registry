package com.example.AuthApp.Registry.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.AuthApp.Registry.DTOs.userDto;
import com.example.AuthApp.Registry.Services.AuthService;

@RestController
@RequestMapping("/Auth")
public class AuthController {
	@Autowired
	private AuthService authService;
	@PostMapping("/register")
	public ResponseEntity<userDto> register(@RequestBody userDto user){
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.Register(user));
	}
}
