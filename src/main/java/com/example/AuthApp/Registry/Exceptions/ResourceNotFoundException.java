package com.example.AuthApp.Registry.Exceptions;

public class ResourceNotFoundException extends RuntimeException {
	
	public ResourceNotFoundException() {
		super("User With This Details Does Not Exists ");
	}
	public ResourceNotFoundException(String message) {
		super(message);
	}
}
