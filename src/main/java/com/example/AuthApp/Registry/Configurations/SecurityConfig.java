package com.example.AuthApp.Registry.Configurations;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.helpers.AbstractLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.client.AbstractHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2ClientConfigurer.AuthorizationCodeGrantConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.AuthApp.Registry.Security.jwtAuthenticationFitler;
import com.fasterxml.jackson.databind.ObjectMapper;


@Configuration
public class SecurityConfig {
	@Autowired
	private jwtAuthenticationFitler authenticationFitler;
	@Autowired
	private ObjectMapper  mapper;
	@Bean
	public PasswordEncoder  encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration con) throws Exception{
			return con.getAuthenticationManager();
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity security) throws Exception {
		System.out.println("******** SECURITY CONFIG LOADED ********");
		security.csrf(AbstractHttpConfigurer::disable)
		.cors(Customizer.withDefaults())
		.sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(x->x.requestMatchers("/Auth/**").permitAll().anyRequest().authenticated())
		.exceptionHandling(x->x.authenticationEntryPoint((request, response, authException) -> {
			authException.printStackTrace();
			response.setStatus(401);
			response.setContentType("application/json");
			String message="Unauthorized Access"+authException.getMessage();
			String error=(String) request.getAttribute("error");
			if(error!=null) {
				message=error;
			}
			Map<String, String> erroMap=Map.of("message " ,message,"status",String.valueOf(401));
			var mappered=new  ObjectMapper();
			response.getWriter().write(mappered.writeValueAsString(erroMap));
		})).addFilterBefore(authenticationFitler, UsernamePasswordAuthenticationFilter.class);
		return security.build();
	}
	
}



//@Bean
//public UserDetailsService detailsService() {
//	User.UserBuilder builder= User.withDefaultPasswordEncoder();
//	UserDetails u1=builder.username("Rahul").password("abc").roles("ADMIN").build();
//	UserDetails u2=builder.username("keshav").password("abc").roles("CUSTOMER").build();
//	UserDetails u3=builder.username("mukesh").password("abc").roles("ADMIN").build();
//	return new InMemoryUserDetailsManager(u1,u2,u3);
//}