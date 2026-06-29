package com.example.AuthApp.Registry.Entities;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="Refresh_Token", indexes = {
		@Index(name="refresh_token_index",columnList="jti",unique=true),
		@Index(name="Id",columnList="id")
})
public class RefreshToken {
	@Id
	@GeneratedValue(strategy=GenerationType.UUID)
private UUID id;
@Column(nullable=false, updatable=false,unique=true)
private String jti;
@ManyToOne(optional=false,fetch=FetchType.LAZY)
@JoinColumn(nullable=false, updatable=false)
private User user;
@Column(nullable=false)
private boolean isRevoked;
@Column(nullable=false)
private Instant willExpireAt;
@Column(nullable=false, updatable=false)
@CreationTimestamp
private Instant CreatedAt;
@UpdateTimestamp
@Column(nullable=false)
private Instant UpdatedAt;
private String isReplacedWith;

}
