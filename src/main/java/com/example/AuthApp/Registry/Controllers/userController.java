package com.example.AuthApp.Registry.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.AuthApp.Registry.DTOs.userDto;
import com.example.AuthApp.Registry.Services.UserService;
import com.example.AuthApp.Registry.Services.Impls.serviceImpl;

@RestController
@RequestMapping("/Api")
public class userController {
	@Autowired
	private UserService userService;
	@PostMapping
	public ResponseEntity<userDto> createUser(@RequestBody userDto user) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(user));
	}
	@GetMapping("/Email/{email}")
	 public ResponseEntity<userDto>  getUserByEmail(@PathVariable String email) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getUserByEmail(email));
	}
	 @PutMapping("/{userId}")
	 public ResponseEntity<userDto>  updateUser(@RequestBody userDto user,@PathVariable  String userId) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.updateUser(user, userId));
	}
	 @DeleteMapping("/{userId}")
	 public ResponseEntity<String> deleteUser(@PathVariable String userId) {
			return ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser(userId));
	}
	 @GetMapping("/{userId}")
	 public ResponseEntity<userDto>  getUserById(@PathVariable String userId) {
			return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(userId));
	}
	 @GetMapping
	 public ResponseEntity< Iterable<userDto>> getAllUsers(){
			return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
	}
	 
	 
}
