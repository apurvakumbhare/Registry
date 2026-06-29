package com.example.AuthApp.Registry.Security;


import org.apache.catalina.filters.ExpiresFilter.XHttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

@Service
@Getter
@Setter
public class CookieService {
   private String refreshTokenCookieName;
   private boolean CookieSecure;
   private boolean CookieHttpOnly;
   private String CookieSameSite;
   private String CookieDomain;
   
   public CookieService(
		@Value("${security.jwt.refresh-token-cookie-name}")   String refreshTokenCookieName,
		@Value("${security.jwt.cookie-secure}")   boolean CookieSecure,
		@Value("${security.jwt.cookie-http-only}")  boolean CookieHttpOnly, 
		@Value("${security.jwt.cookie-same-site}")  String CookieSameSite,
		@Value("${security.jwt.cookie-domain}")   String CookieDomain) {
	   this.refreshTokenCookieName=refreshTokenCookieName;
	   this.CookieDomain=CookieDomain;
	   this.CookieSameSite=CookieSameSite;
	   this.CookieSecure=CookieSecure;
	   this.CookieHttpOnly=CookieHttpOnly;
   }
   
   public void  AttachCookie(HttpServletResponse response,String token,int maxAge) {
	  ResponseCookie.ResponseCookieBuilder cookie =ResponseCookie.from(refreshTokenCookieName, token).httpOnly(CookieHttpOnly).sameSite(CookieSameSite).secure(CookieSecure).maxAge(maxAge).path("/");
	  if(CookieDomain!=null || !CookieDomain.isBlank()) {
		  cookie.domain(CookieDomain);
	  }
	  ResponseCookie build=cookie.build();
	  response.addHeader(HttpHeaders.SET_COOKIE, build.toString());
   }
   public void RevokeCookie(HttpServletResponse response) {
	   ResponseCookie.ResponseCookieBuilder cookie =ResponseCookie.from(refreshTokenCookieName, "").httpOnly(CookieHttpOnly).sameSite(CookieSameSite).secure(CookieSecure).maxAge(0).path("/");
		  if(CookieDomain!=null || !CookieDomain.isBlank()) {
			  cookie.domain(CookieDomain);
		  }
		  ResponseCookie build=cookie.build();
		  response.addHeader(HttpHeaders.SET_COOKIE, build.toString());
   }
   public void addNoStoreHeaders(HttpServletResponse response) {
	   response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
	   response.setHeader("pragma", "no-cache");
   }
   
}
