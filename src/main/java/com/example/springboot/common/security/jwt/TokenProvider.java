package com.example.springboot.common.security.jwt;

import com.example.springboot.user.domain.token.TokenEntity;
import com.example.springboot.user.domain.token.TokenRepository;
import com.example.springboot.user.model.token.InquiryTokenDTO;
import com.example.springboot.common.security.auth.CustomUserDetailService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenProvider {
	private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
	private CustomUserDetailService userDetailsService;
	private TokenRepository tokenRepository;
	@Value("${SECRET_HEADER}") 		 private String headerKey;
	@Value("${SECRET_KEY}") 		 private String secretKey;
	@Value("${SECRET_TYPE}") 		 private String typeKey;
	@Value("${SECRET_TIME_ACCESS}")  private long accessValidTime;
	@Value("${SECRET_TIME_REFRESH}") private long refreshValidTime;

	@Autowired
	public TokenProvider(CustomUserDetailService userDetailsService, TokenRepository tokenRepository) {
		this.userDetailsService = userDetailsService;
		this.tokenRepository = tokenRepository;
	}

	public InquiryTokenDTO generateToken(String userPk) {
		Date now = new Date();
		String accessToken = generateAccessToken(userPk);
		String refreshToken = Jwts.builder()
				.setSubject(userPk)
				.setExpiration(new Date(now.getTime() + refreshValidTime*1000))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();

		return InquiryTokenDTO.builder()
				.email(userPk)
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}
	public String generateAccessToken(String userPk) {
		Date now = new Date();

		return Jwts.builder()
				.setSubject(userPk)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime()+ accessValidTime*1000))
				.signWith(SignatureAlgorithm.HS256, secretKey)
				.compact();
	}

	public String getUserPk(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public Authentication getAuthentication(String access) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(access));
		return new UsernamePasswordAuthenticationToken(userDetails, access, userDetails.getAuthorities());
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (SignatureException se) {
			logger.error("잘못된 서명", se);
		} catch (MalformedJwtException me) {
			logger.error("잘못된 토큰", me);
		} catch (ExpiredJwtException ee) {
			logger.error("만료된 토큰", ee);
			expiredTokenHandler(token);
		} catch (UnsupportedJwtException ue) {
			logger.error("지원되지 않는 토큰", ue);
		} catch (IllegalArgumentException ie) {
			logger.error("비어있는 토큰", ie);
		} catch (NullPointerException ne) {
			logger.error("존재하지 않는 토큰", ne);
		}
		return false;
	}


	public void expiredTokenHandler(String token) {

		String userPk = this.getUserPk(token);
		String key = tokenRepository.findByEmail(userPk).getRefreshToken();
		String access = "";
		if(this.validateToken(key)) {
			access = this.generateAccessToken(userPk);
		}

	}

	public String requestCheckToken(HttpServletRequest request) {
		String token = request.getHeader(headerKey);
		if(token == null || !token.startsWith(typeKey)) {
			return null;
		}
		return token.replace(typeKey, "");
	}

	public void saveRefresh(InquiryTokenDTO inquiryTokenDTO) {
		logger.info("TokenController - saveRefresh() ...");
		tokenRepository.save(TokenEntity.builder()
										.email(inquiryTokenDTO.getEmail())
										.accessToken(inquiryTokenDTO.getAccessToken())
										.refreshToken(inquiryTokenDTO.getRefreshToken())
										.build());
	}
}
