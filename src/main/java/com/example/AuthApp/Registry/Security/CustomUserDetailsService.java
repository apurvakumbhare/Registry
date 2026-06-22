package com.example.AuthApp.Registry.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.AuthApp.Registry.Repositories.userRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	@Autowired
	private userRepository repository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 System.out.println("LOAD USER CALLED WITH : " + username);
		return repository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("Invalid User"));
	}

}
