package com.example.AuthApp.Registry.Controllers;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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
import com.example.AuthApp.Registry.Entities.RefreshToken;
import com.example.AuthApp.Registry.Entities.ResponseTokenRequest;
import com.example.AuthApp.Registry.Entities.TokenResponse;
import com.example.AuthApp.Registry.Entities.User;
import com.example.AuthApp.Registry.Repositories.RefreshTokenRepository;
import com.example.AuthApp.Registry.Repositories.userRepository;
import com.example.AuthApp.Registry.Security.CookieService;
import com.example.AuthApp.Registry.Security.JWTService;
import com.example.AuthApp.Registry.Services.AuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/Auth")
public class AuthController {
	@Autowired 
	private CookieService cookieService;
	@Autowired
	private RefreshTokenRepository refreshTokenRepository;
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
	public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request,HttpServletResponse loginResponse){
		Authentication authentication =authenticate(request);
		User user=repository.findByEmail(request.Email()).orElseThrow(()-> new BadCredentialsException("Invalid Username Or Password"));
		if(!user.isEnable()) {
			throw new DisabledException("User is Disabled");
		}
		
		String jti=UUID.randomUUID().toString();
		RefreshToken token=RefreshToken.builder().jti(jti).user(user).willExpireAt(Instant.now().plusSeconds(60)).isRevoked(false).build();
		refreshTokenRepository.save(token);
		String AccessToken=jwtService.generateAccessToken(user);
		String refreshtoken=jwtService.generateRefreshToken(user, jti);
		cookieService.AttachCookie(loginResponse, refreshtoken, (int) jwtService.getRefreshTtlSeconds());
		cookieService.addNoStoreHeaders(loginResponse);

		TokenResponse response=TokenResponse.of(AccessToken,refreshtoken,  jwtService.parse(AccessToken).getPayload().get("typ").toString(),jwtService.getAccessTtlSeconds(),mapper.map(user, userDto.class));
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refresh(@RequestBody(required=false) ResponseTokenRequest token,HttpServletRequest request,HttpServletResponse response){
	 String existingRefreshToekn=readRefreshTokenRequest(token, request).orElseThrow(()->new BadCredentialsException("Invalid refreshToken"));	
	 if(!jwtService.isRefreshToken(existingRefreshToekn)) {
		 throw new  BadCredentialsException("Invalid refreshToken type");
	 }
	 String jti =jwtService.getJTI(existingRefreshToekn);
	 UUID userUuid=jwtService.getUUID(existingRefreshToekn);
	 RefreshToken stored=refreshTokenRepository.findByJti(jti).orElseThrow(()-> new BadCredentialsException("Refresh Token is invalid"));
	 if(stored.getWillExpireAt().isBefore(Instant.now())) {
		 throw new BadCredentialsException("Refresh Token is Expired"); 
	 }
	 if(stored.isRevoked()) {
		 throw new BadCredentialsException("Refresh Token is revoked"); 
	 }
	 if(!stored.getUser().getId().equals(userUuid)) {
		 throw new BadCredentialsException("Refresh Token does not belongs to thids user"); 
	 }
	 stored.setRevoked(true);
	 String newjti=UUID.randomUUID().toString();
	 stored.setIsReplacedWith(newjti);
	 refreshTokenRepository.save(stored);
	 
	 User user=stored.getUser();
	 
	 var newRefreshedToken=RefreshToken.builder().jti(newjti).user(user).isRevoked(false).build();
	 refreshTokenRepository.save(newRefreshedToken);
	 
	 String newAccessToken=jwtService.generateAccessToken(user);
		String newrefreshtoken=jwtService.generateRefreshToken(user, newRefreshedToken.getJti());
		cookieService.AttachCookie(response, newrefreshtoken, (int) jwtService.getRefreshTtlSeconds());
		cookieService.addNoStoreHeaders(response);

		TokenResponse tokenresponse=TokenResponse.of(newAccessToken,newrefreshtoken,  jwtService.parse(newAccessToken).getPayload().get("typ").toString(),jwtService.getAccessTtlSeconds(),mapper.map(user, userDto.class));
		
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(tokenresponse);
	}
		
	
	private Optional<String> readRefreshTokenRequest(ResponseTokenRequest token, HttpServletRequest request) {
		
		//from http request
		if(request.getCookies()!=null) {
			Optional<String> refreshToken=Arrays.stream(request.getCookies()).filter(c->cookieService.getRefreshTokenCookieName().equals(c.getName()))
			.map(Cookie::getValue)
			.filter(v->!v.isBlank())
			.findFirst();
			if(refreshToken.isPresent()) {
				return refreshToken;
			}
			}
		//from body
		if(token!=null && token.refreshToken()!=null && !token.refreshToken().isBlank()) {
			return Optional.of(token.refreshToken());
		}
		//from Authorization Header
		String header=request.getHeader(HttpHeaders.AUTHORIZATION);
		if(header!=null && header.regionMatches(true,0, "Bearer", 0,7)) {
			String candidate=header.substring(7).trim();
			if(!candidate.isEmpty()) {
				try {
					if(jwtService.isRefreshToken(candidate)) {
						return Optional.of(candidate);
					}
				}
				catch(Exception ignored) {
					
				}
			}
		}
		return Optional.empty();
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
