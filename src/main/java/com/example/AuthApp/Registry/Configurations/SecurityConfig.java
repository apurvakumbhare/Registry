package com.example.AuthApp.Registry.Configurations;

import org.springframework.boot.autoconfigure.http.client.AbstractHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
public class SecurityConfig {
		
	@Bean
	public UserDetailsService detailsService() {
		User.UserBuilder builder= User.withDefaultPasswordEncoder();
		UserDetails u1=builder.username("Rahul").password("abc").roles("ADMIN").build();
		UserDetails u2=builder.username("keshav").password("abc").roles("CUSTOMER").build();
		UserDetails u3=builder.username("mukesh").password("abc").roles("ADMIN").build();
		return new InMemoryUserDetailsManager(u1,u2,u3);
	}
}
