package com.example.springboot.common.security.jwt;

import com.example.springboot.common.config.properties.TokenProperties;
import com.example.springboot.user.domain.token.TokenEntity;
import com.example.springboot.user.domain.token.TokenRepository;
import com.example.springboot.user.model.token.FirstTimeTokenDTO;
import com.example.springboot.user.model.token.ReIssuanceTokenDTO;
import com.example.springboot.user.model.token.ValidateTokenDTO;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
public class TokenProvider {
	private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
	private static final String headerKeyAccess = TokenProperties.HEADER_KEY_ACCESS;
	private static final String typeKeyAccess = TokenProperties.SECRET_TYPE_ACCESS;
	private static final String typeKeyRefresh = TokenProperties.SECRET_TYPE_REFRESH;
	private static final long accessValidTime = TokenProperties.SECRET_TIME_ACCESS;
	private static final long refreshValidTime = TokenProperties.SECRET_TIME_REFRESH;
	private final String secretKey;
	private final TokenRepository tokenRepository;

	@Autowired
	public TokenProvider(TokenRepository tokenRepository) {
		this.secretKey = Base64.getEncoder().encodeToString(TokenProperties.SECRET_KEY.getBytes());
		this.tokenRepository = tokenRepository;
	}

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
			logger.error("잘못된 서명 TokenProvider - validateToken()", se);
		} catch (MalformedJwtException me) {
			logger.error("잘못된 토큰 TokenProvider - validateToken()", me);
		} catch (ExpiredJwtException ee) {
			logger.error("만료된 토큰 TokenProvider - validateToken()", ee);
		} catch (UnsupportedJwtException ue) {
			logger.error("지원되지 않는 토큰 TokenProvider - validateToken()", ue);
		} catch (IllegalArgumentException ie) {
			logger.error("비어있는 토큰 TokenProvider - validateToken()", ie);
		} catch (NullPointerException ne) {
			logger.error("존재하지 않는 토큰 TokenProvider - validateToken()", ne);
		}
		return false;
	}

	public ValidateTokenDTO requestCheckToken(HttpServletRequest request) {
		logger.info("TokenProvider - requestCheckToken() ...");

		try {
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
		} catch (NullPointerException ne) {
			logger.error("요청 값이 비어 있습니다. TokenProvider - requestCheckToken()");
		} catch (Exception e) {
			logger.error("TokenProvider - requestCheckToken()", e);
		}
		return ValidateTokenDTO.builder().code(2).token("").build();
	}

	public boolean saveRefresh(ReIssuanceTokenDTO reIssuanceTokenDTO) {
		logger.info("TokenProvider - saveRefresh() ...");
		try {
			TokenEntity tokenEntity = tokenRepository.save(TokenEntity.builder()
																		.email(reIssuanceTokenDTO.getEmail())
																		.refreshToken(reIssuanceTokenDTO.getRefreshToken())
																		.build());
			if(tokenEntity.getEmail() != null) return true;
		} catch (NullPointerException ne) {
			logger.error("토큰 셋이 비어있습니다. TokenProvider - saveRefresh()", ne);
		} catch (Exception e) {
			logger.error("토큰 셋 저장 실패 TokenProvider - saveRefresh()", e);
		}
		return false;
	}

	public boolean validateExistingToken(String token) {
		logger.info("TokenProvider - validateExistingToken() ...");
		try {
			if(this.validateToken(token)) {
				String userPk = this.getUserPk(token);
				String existingToken = tokenRepository.findByEmail(userPk).getRefreshToken();
				if(existingToken.equals(token)) return true;
			}
		} catch (Exception e) {
			logger.error("토큰 저장소 비교 검증 에러 TokenProvider - validateExistingToken()", e);
		}
		return false;
	}

	public boolean updateRefresh(ReIssuanceTokenDTO reIssuanceTokenDTO) {
		logger.info("TokenProvider - updateRefresh() ...");
		try {
			Integer result = tokenRepository.updateToken(reIssuanceTokenDTO.getRefreshToken(), reIssuanceTokenDTO.getEmail());
			if(result > 0) return true;
		} catch (NullPointerException ne) {
			logger.error("토큰 저장소가 비어있습니다. TokenProvider - updateRefresh()", ne);
		} catch (Exception e) {
			logger.error("SERVER ERROR TokenProvider - updateRefresh()", e);
		}
		return false;
	}
}
