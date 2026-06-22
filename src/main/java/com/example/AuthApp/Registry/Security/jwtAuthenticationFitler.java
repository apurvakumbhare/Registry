package com.example.AuthApp.Registry.Security;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.apache.tomcat.util.log.UserDataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.AuthApp.Registry.Repositories.userRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class jwtAuthenticationFitler extends OncePerRequestFilter{
	 private static final Logger logger =
	            LoggerFactory.getLogger(jwtAuthenticationFitler.class);

	@Autowired
	private JWTService jwtService;
	@Autowired
	private userRepository repository;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String Header=request.getHeader("Authorization");
		logger.info("Authentication header" + Header);
		if(Header!=null && Header.startsWith("Bearer ")) {
			String token=Header.substring(7);
		try {
			if(!jwtService.isAccessToken(token)) {
				filterChain.doFilter(request, response);
			
				return;
			}
			Jws<Claims> parse=jwtService.parse(token);
			Claims payload=parse.getPayload();
			String userId = payload.getSubject();
			UUID userUuid=UUID.fromString(userId);
			repository.findById(userUuid).ifPresent(user->{
				if(!user.isEnable()) {
					try {
						filterChain.doFilter(request, response);
					}catch (IOException e) {
						throw new RuntimeException(e);
						}
						catch (ServletException e) {
							throw new RuntimeException(e);
						}
					return;
				}
				
				List<SimpleGrantedAuthority> role=user.getRoles()==null? List.of():user.getRoles().stream()
						.map(x->new SimpleGrantedAuthority(x.getName())).toList();
			
			UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(user.getEmail(), null,role);
			authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			});
		}
		catch (ExpiredJwtException e) {
		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    response.getWriter().write("Token Expired");
		    return;
		}
		catch (Exception e) {
		    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		    response.getWriter().write("Invalid Token");
		    return;
		}
		}
		 filterChain.doFilter(request, response);
	}
	@Override
		protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException  {
			return request.getRequestURI().startsWith("/Auth/login");
			}
	
}


