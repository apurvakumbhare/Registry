package com.example.AuthApp.Registry.DTOs;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.example.AuthApp.Registry.Entities.Role;
import com.example.AuthApp.Registry.Enum.Provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class userDto {
	private UUID id;
	private String email;
	private String name;
	private String password;
	private String image;
	private boolean enable;
	private Instant createdAt;
	private Instant updatedAt;
	private Provider provider;
	private Set<Role> roles=new HashSet<>();
}
