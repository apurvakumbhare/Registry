package com.example.AuthApp.Registry.Services;

import org.springframework.stereotype.Service;

import com.example.AuthApp.Registry.DTOs.userDto;

@Service
public class serviceImpl implements UserService{

	@Override
	public userDto createUser(userDto user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public userDto getUserByEmail(String email) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public userDto updateUser(userDto user, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUser(String userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public userDto getUserById(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<userDto> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

}
