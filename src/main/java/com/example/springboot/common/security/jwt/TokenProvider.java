package com.example.springboot.common.security.jwt;

import com.example.springboot.user.domain.token.TokenEntity;
import com.example.springboot.user.domain.token.TokenRepository;
import com.example.springboot.user.model.token.FirstTimeTokenDTO;
import com.example.springboot.user.model.token.ReIssuanceTokenDTO;
import com.example.springboot.user.model.token.ValidateTokenDTO;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TokenProvider {
	private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
	private final TokenRepository tokenRepository;
	private static final String headerKeyAccess = TokenProperties.HEADER_KEY_ACCESS;
	private static final String typeKeyAccess = TokenProperties.SECRET_TYPE_ACCESS;
	private static final String typeKeyRefresh = TokenProperties.SECRET_TYPE_REFRESH;
	private static final long accessValidTime = TokenProperties.SECRET_TIME_ACCESS;
	private static final long refreshValidTime = TokenProperties.SECRET_TIME_REFRESH;
	private static final String secretKey = Base64.getEncoder().encodeToString(TokenProperties.SECRET_KEY.getBytes());

	public FirstTimeTokenDTO generateToken(String userPk) {
		logger.info("TokenProvider - generateToken() ...");
		Date now = new Date();
		String accessToken = generateAccessToken(userPk);
		String refreshToken = Jwts.builder()
				.setSubject(userPk)
				.setExpiration(new Date(now.getTime() + refreshValidTime))
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();

		return FirstTimeTokenDTO.builder().accessToken(accessToken)
				.reIssuanceTokenDTO(ReIssuanceTokenDTO.builder()
														.email(userPk)
														.refreshToken(refreshToken)
														.build()).build();
	}
	public String generateAccessToken(String userPk) {
		logger.info("TokenProvider - generateAccessToken() ...");
		Date now = new Date();

		Map<String, Object> payload = new HashMap<>();
		payload.put("userPk", userPk);


		return Jwts.builder()
				.setSubject(userPk)
				.setIssuedAt(now)
				.setExpiration(new Date(now.getTime()+ accessValidTime))
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();
	}

	public String getUserPk(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateToken(String token) {
		logger.info("TokenProvider - validateToken() ...");
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (SignatureException se) {
			logger.error("잘못된 서명", se);
		} catch (MalformedJwtException me) {
			logger.error("잘못된 토큰", me);
		} catch (ExpiredJwtException ee) {
			logger.error("만료된 토큰", ee);
		} catch (UnsupportedJwtException ue) {
			logger.error("지원되지 않는 토큰", ue);
		} catch (IllegalArgumentException ie) {
			logger.error("비어있는 토큰", ie);
		} catch (NullPointerException ne) {
			logger.error("존재하지 않는 토큰", ne);
		}
		return false;
	}

	public ValidateTokenDTO requestCheckToken(HttpServletRequest request) {
		logger.info("TokenProvider - requestCheckToken() ...");

		String token = request.getHeader(headerKeyAccess);

		if(token.startsWith(typeKeyAccess)) {
			return ValidateTokenDTO.builder()
									.code(0)
									.token(token.replace(typeKeyAccess, ""))
									.build();
		}
		if(token.startsWith(typeKeyRefresh)){
			return ValidateTokenDTO.builder()
									.code(1)
									.token(token.replace(typeKeyRefresh, "")).build();
		}
		return ValidateTokenDTO.builder().code(2).token("").build();
	}

	public boolean saveRefresh(FirstTimeTokenDTO firstTimeTokenDTO) {
		logger.info("TokenController - saveRefresh() ...");
		try {
			tokenRepository.save(TokenEntity.builder()
					.email(firstTimeTokenDTO.getReIssuanceTokenDTO().getEmail())
					.accessToken(firstTimeTokenDTO.getAccessToken())
					.refreshToken(firstTimeTokenDTO.getReIssuanceTokenDTO().getRefreshToken())
					.build());
			return true;
		} catch (Exception e) {
			logger.error("토큰 셋 저장 실패", e);
		}
		return false;
	}

	public boolean validateRefreshToken(String token) {
		logger.info("TokenProvider - validateRefreshToken() ...");
		try {
			if(this.validateToken(token)) {
				String userPk = this.getUserPk(token);
				String existingToken = tokenRepository.findByEmail(userPk).getRefreshToken();
				if(existingToken.equals(token)) return true;
			}
		} catch (Exception e) {
			logger.error("리프레쉬 토큰 검증 에러", e);
		}
		return false;
	}

	public boolean changeAccessToken(String token) {
		try {
			String userPk = this.getUserPk(token);
			String value = tokenRepository.updateByAccessToken(userPk);
			if(value.equals(token)) return true;
		} catch (Exception e) {
			logger.error("액세스 토큰 삭제-저장 / 업데이트 에러", e);
		}
		return false;
	}
}
