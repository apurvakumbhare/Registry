package com.example.AuthApp.Registry.Entities;

import com.example.AuthApp.Registry.DTOs.userDto;

public record TokenResponse(
		String accessToken,
		String refreshToken,
		String tokenType,
		long expiresIn,
		userDto user ) {
public static TokenResponse of(String accessToken,String refreshToken,String token,long expiresIn,userDto user){
	return new TokenResponse(accessToken, refreshToken, token, expiresIn, user);
}
}
