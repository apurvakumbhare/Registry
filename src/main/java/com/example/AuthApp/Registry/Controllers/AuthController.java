package com.example.AuthApp.Registry.Controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.AuthApp.Registry.DTOs.userDto;
import com.example.AuthApp.Registry.Entities.LoginRequest;
import com.example.AuthApp.Registry.Entities.TokenResponse;
import com.example.AuthApp.Registry.Entities.User;
import com.example.AuthApp.Registry.Repositories.userRepository;
import com.example.AuthApp.Registry.Security.JWTService;
import com.example.AuthApp.Registry.Services.AuthService;

@RestController
@RequestMapping("/Auth")
public class AuthController {
	@Autowired
	private ModelMapper mapper;
	@Autowired
	private JWTService jwtService;
	@Autowired
	private AuthenticationManager  authenticationManager;
	@Autowired
	private userRepository repository;
	@Autowired
	private AuthService authService;
	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request){
		Authentication authentication =authenticate(request);
		User user=repository.findByEmail(request.Email()).orElseThrow(()-> new BadCredentialsException("Invalid Username Or Password"));
		if(!user.isEnable()) {
			throw new DisabledException("User is Disabled");
		}
		String AccessToken=jwtService.generateAccessToken(user);
		TokenResponse response=TokenResponse.of(AccessToken, "",  jwtService.parse(AccessToken).getPayload().get("typ").toString(),jwtService.getAccessTtlSeconds(),mapper.map(user, userDto.class));
	return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	public Authentication authenticate(LoginRequest request){
		try {
			return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.Email(), request.PassWord()));
		}catch (Exception e) {
			throw new BadCredentialsException("Invalid Credentials");
		}
	}
	@PostMapping("/register")
	public ResponseEntity<userDto> register(@RequestBody userDto user){
	    System.out.println("******** REGISTER API HIT ********");
		return ResponseEntity.status(HttpStatus.CREATED).body(authService.Register(user));
	}
}
