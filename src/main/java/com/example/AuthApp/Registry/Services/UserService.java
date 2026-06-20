package com.example.AuthApp.Registry.Services;

import com.example.AuthApp.Registry.DTOs.userDto;

public interface UserService {
	userDto createUser(userDto user);
	userDto getUserByEmail(String email);
	userDto updateUser(userDto user,String userId);
	String deleteUser(String userId);
	userDto getUserById(String userId);
	Iterable<userDto> getAllUsers();
}
