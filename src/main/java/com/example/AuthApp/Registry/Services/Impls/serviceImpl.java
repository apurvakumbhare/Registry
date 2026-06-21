package com.example.AuthApp.Registry.Services.Impls;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.AuthApp.Registry.DTOs.userDto;
import com.example.AuthApp.Registry.Entities.User;
import com.example.AuthApp.Registry.Enum.Provider;
import com.example.AuthApp.Registry.Exceptions.ResourceNotFoundException;
import com.example.AuthApp.Registry.Repositories.userRepository;
import com.example.AuthApp.Registry.Services.UserService;

import jakarta.transaction.Transactional;

@Service
public class serviceImpl implements UserService{
	@Autowired
	public userRepository repository;
	@Autowired
	private ModelMapper modelMapper;
	@Override
	@Transactional
	public userDto createUser(userDto user) {
		if(user.getEmail()==null || user.getEmail().isBlank()) {
			throw new IllegalArgumentException("Email is required ");
		}
		if(repository.existsByEmail(user.getEmail())) {
			throw new IllegalArgumentException("User with Given Email Already Exists");
		}
		User entity = modelMapper.map(user, User.class);

		if(entity.getProvider() == null) {
		    entity.setProvider(Provider.LOCAL);
		}

		User saved=repository.saveAndFlush(entity);
		return modelMapper.map(saved, userDto.class);
	}

	@Override
	public userDto getUserByEmail(String email) {
		User user=repository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User with Given Email does not Exists"));
		return modelMapper.map(user,userDto.class);
	}

	@Override
	public userDto updateUser(userDto user, String userId) {
		UUID Id=UUID.fromString(userId);
		
		
			User toBeSaved=repository.findById(Id).orElseThrow(()->new ResourceNotFoundException("User With this 'USERID' Does Not Exists "));
			toBeSaved.setImage(user.getImage());
			toBeSaved.setEnable(user.isEnable());
			toBeSaved.setName(user.getName());
			toBeSaved.setPassword(user.getPassword());
			if(user.getProvider() == null) {
			    toBeSaved.setProvider(Provider.LOCAL);
			}else toBeSaved.setProvider(user.getProvider());
			toBeSaved.setRoles(user.getRoles());
			return modelMapper.map(repository.save(toBeSaved),userDto.class);
	}

	@Override
	public String deleteUser(String userId) {
		UUID Id=UUID.fromString(userId);
		if(!repository.existsById(Id))
			throw new ResourceNotFoundException("User With this Email Does Not Exists ");
			repository.deleteById(Id);
		return "User Deleted Successfully";
		}

	@Override
	public userDto getUserById(String userId) {
		UUID Id=UUID.fromString(userId);
		User user=repository.findById(Id).orElseThrow(()->new ResourceNotFoundException("User With this UserID Does Not Exists "));

		return modelMapper.map(user,userDto.class);
	}

	@Override
	@Transactional
	public Iterable<userDto> getAllUsers() {
		
	
	return repository.findAll().stream().map(user->modelMapper.map(user, userDto.class)).toList();
	}
}
