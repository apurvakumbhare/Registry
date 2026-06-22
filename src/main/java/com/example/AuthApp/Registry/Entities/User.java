package com.example.AuthApp.Registry.Entities;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.AuthApp.Registry.Enum.Provider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	@Column(unique = true )
	private String email;
	@Column(length = 500)
	private String name;
	private String password;
	private String image;
	private boolean enable;
	@CreationTimestamp
	private Instant createdAt;
	@UpdateTimestamp
	private Instant updatedAt;
	@Enumerated(EnumType.STRING)
	private Provider provider=Provider.LOCAL;
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_roles_Mapping",joinColumns = @JoinColumn(name = "user_id") ,inverseJoinColumns = @JoinColumn(name="role_id") )
	private Set<Role> roles=new HashSet<>();
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> list=roles.stream().map(x->new SimpleGrantedAuthority(x.getName())).toList();
		return null;
	}
	@Override
	public String getUsername() {
		return this.email;
	}
	
	
}
