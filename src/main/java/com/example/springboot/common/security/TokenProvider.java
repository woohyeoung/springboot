package com.example.springboot.common.security;

import com.example.springboot.user.model.AccessResponseDTO;
import com.example.springboot.user.model.TokenResponseDTO;
import com.example.springboot.user.service.CustomUserDetailService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

@Component
@AllArgsConstructor
public class TokenProvider implements InitializingBean {
	private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
	private final String HEADER_KEY;
	private final String SECRET_KEY;
	private final String TYPE_KEY;
	private final long ACCESS_VALID_TIME = 1800000L;
	private final long REFRESH_VALID_TIME = 86400000L;
	private Key key;
	private CustomUserDetailService userDetailsService;

	@Autowired
	public TokenProvider(@Value("${SECRET_KEY}") String secretKey,
						 @Value("${SECRET_HEADER}") String headerKey,
						 @Value("${SECRET_TYPE}") String typeKey) {
		this.TYPE_KEY = typeKey;
		this.SECRET_KEY = secretKey;
		this.HEADER_KEY = headerKey;
	}

	@Override
	public void afterPropertiesSet() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		key = Keys.hmacShaKeyFor(keyBytes);
	}

	public AccessResponseDTO generateToken(String userPk) {
		Date now = new Date();
		String accessToken = generateAccessToken(userPk);
		String refreshToken = Jwts.builder()
				.setSubject(userPk)
				.setExpiration(new Date(now.getTime() + REFRESH_VALID_TIME))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();

		return new AccessResponseDTO(accessToken, TokenResponseDTO.builder()
																.email(userPk)
																.refreshToken(refreshToken)
																.RTExp(REFRESH_VALID_TIME)
																.build());
	}
	public String generateAccessToken(String userPk) {
		Date now = new Date();

		return Jwts.builder()
				.setSubject(userPk)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime()+ ACCESS_VALID_TIME))
				.signWith(key, SignatureAlgorithm.HS256)
				.compact();
	}

	public String getUserPk(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	public Authentication getAuthentication(String access) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(access));
		return new UsernamePasswordAuthenticationToken(userDetails, access, userDetails.getAuthorities());
	}

	public boolean validateToken(String token) {
		try {
			System.out.println("hihi" + token);
			Jwts.parserBuilder().setSigningKey(key).build().parse(token);
			return true;
		} catch (io.jsonwebtoken.security.SecurityException se) {
			logger.info("잘못된 서명", se);
		} catch (MalformedJwtException me) {
			logger.info("잘못된 토큰", me);
		} catch (ExpiredJwtException ee) {
			logger.info("만료된 토큰", ee);
		} catch (UnsupportedJwtException ue) {
			logger.info("지원되지 않는 토큰", ue);
		} catch (IllegalArgumentException ie) {
			logger.info("비어있는 토큰", ie);
		} catch (NullPointerException ne) {
			logger.info("존재하지 않는 토큰", ne);
		}
		return false;
	}

	public String resolveToken(HttpServletRequest request) {
		String token = request.getHeader(HEADER_KEY);
		if(StringUtils.hasText(token) && token.startsWith(TYPE_KEY))
			return token.substring(7);
		return null;
	}
}
