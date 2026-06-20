package com.example.AuthApp.Registry.DTOs;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class roleDto {
	private UUID id=UUID.randomUUID();
	private String name;
}

