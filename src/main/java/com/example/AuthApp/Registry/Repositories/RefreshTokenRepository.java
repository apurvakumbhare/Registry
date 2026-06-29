package com.example.AuthApp.Registry.Repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.AuthApp.Registry.Entities.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
	public Optional<RefreshToken> findByJti(String jti);
}
