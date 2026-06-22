package com.example.AuthApp.Registry.Security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.example.AuthApp.Registry.Entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
@Service
@Getter
@Setter
public class JWTService {

	private final SecretKey jwtSecret;
	private final long accessTtlSeconds;
	private final long refreshTtlSeconds;
	private final String issuer;
	public JWTService(@Value("${security.jwt.secret}") String jwtSecret,
			@Value("${security.jwt.access-ttl-seconds}") long accessTtlSeconds,
			@Value("${security.jwt.refresh-ttl-seconds}")long refreshTtlSeconds,
			@Value("${security.jwt.issuer}") String issuer) {
		if(jwtSecret==null || jwtSecret.length()<64) {
			throw new IllegalArgumentException("Secret Illegal");
		}
		this.jwtSecret = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
		this.accessTtlSeconds = accessTtlSeconds;
		this.refreshTtlSeconds = refreshTtlSeconds;
		this.issuer = issuer;
	}
	
	public String generateAccessToken(User user) {
		Instant now=Instant.now();
		List<SimpleGrantedAuthority> role=user.getRoles()==null? List.of():user.getRoles().stream().map(x->new SimpleGrantedAuthority(x.getName())).toList();
	String token= Jwts.builder()
				.id(UUID.randomUUID().toString())
				.claims(Map.of(
						"email", user.getEmail(),
						"typ", "access",
						"roles", role))
				.subject(user.getId().toString())
				.issuedAt(Date.from(now))
				.issuer(issuer)
				.expiration(Date.from(now.plusSeconds(accessTtlSeconds)))
				.signWith(jwtSecret,SignatureAlgorithm.HS512)
				.compact();

    System.out.println("Generated token = " + token);
    System.out.println("Now = " + now);
    System.out.println("Expires at = " + now.plusSeconds(accessTtlSeconds));
    return token;
	}
	public String generateRefreshToken(User user, String jti) {
		Instant now=Instant.now();
		List<SimpleGrantedAuthority> role=user.getRoles().stream().map(x->new SimpleGrantedAuthority(issuer)).toList();
		return Jwts.builder()
				.id(jti)
				.claims(Map.of(
						"email", user.getEmail(),
						"typ", "refresh",
						"roles", role))
				.subject(user.getId().toString())
				.issuedAt(Date.from(now))
				.issuer(issuer)
				.expiration(Date.from(now.plusSeconds(refreshTtlSeconds)))
				.signWith(jwtSecret,SignatureAlgorithm.HS512)
				.compact();
				
	}
	//parse the token
	public Jws<Claims> parse(String token){
		try {
			return Jwts.parser().verifyWith(jwtSecret).build().parseSignedClaims(token);
		}catch (Exception e) {
		throw e;
		}
	}
	//is Access Token 
	public boolean isAccessToken(String token) {
		Claims claim=parse(token).getPayload();
		return claim.get("typ").equals("access");
	}
	//is Refresh Token 
	public boolean isRefreshToken(String token) {
		Claims claim=parse(token).getPayload();
		return claim.get("typ").equals("refresh");
	}
	//get UUID
	public UUID getUUID(String token ) {
		Claims claim=parse(token).getPayload();
		return UUID.fromString(claim.getId());
	}
	//get Subject
		public String getJTI(String token ) {
			Claims claim=parse(token).getPayload();
			return claim.getId();
		}
	//get Roles
		public List<String> getRoles(String token ){
			Claims claim=parse(token).getPayload();
			return (List<String>) claim.get("roles");
		}
	//get Email
			public String getEmails(String token ){
				Claims claim=parse(token).getPayload();
				return claim.get("email").toString();
			}
	}
