package com.example.AuthApp.Registry.Services.Impls;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.AuthApp.Registry.DTOs.userDto;
import com.example.AuthApp.Registry.Services.AuthService;
import com.example.AuthApp.Registry.Services.UserService;

@Service

public class AuthServiceImpl implements AuthService{
	@Autowired
	private UserService userService;
	@Autowired
	private PasswordEncoder encoder;
	@Override
	public userDto Register(userDto user){
		user.setPassword(encoder.encode(user.getPassword()));
		return userService.createUser(user)	;
	}
}
