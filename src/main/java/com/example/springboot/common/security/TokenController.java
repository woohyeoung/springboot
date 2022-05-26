package com.example.springboot.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenController {

	@Value("${SECRET_KEY}")		 private String secretKey;

	private UserDetailsService userDetailsService;


	public Authentication getAuthentication(String token) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
		return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	}

	public String getUserPk(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public String resolveToken(HttpServletRequest request) {  // HttpServletRequest 라이브러리 추가할것
		return request.getHeader("X-AUTH-TOKEN");
	}

	public boolean validateToken(String jwtToken) {
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
			return !claims.getBody().getExpiration().before(new Date());
		} catch (Exception e) {
			return false;
		}
	}
}
