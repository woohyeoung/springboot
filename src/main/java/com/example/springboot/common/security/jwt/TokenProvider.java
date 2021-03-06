package com.example.springboot.common.security.jwt;

import com.example.springboot.common.config.properties.TokenProperties;
import com.example.springboot.user.domain.token.TokenEntity;
import com.example.springboot.user.domain.token.TokenRepository;
import com.example.springboot.user.model.token.FirstTimeTokenDTO;
import com.example.springboot.user.model.token.ReIssuanceTokenDTO;
import com.example.springboot.user.model.token.ValidateTokenDTO;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@Slf4j
public class TokenProvider {
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
		log.info("TokenProvider - generateToken() ...");
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
		log.info("TokenProvider - generateAccessToken() ...");
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
		log.info("TokenProvider - validateToken() ...");
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
			return true;
		} catch (SignatureException se) {
			log.error("????????? ?????? TokenProvider - validateToken()", se);
		} catch (MalformedJwtException me) {
			log.error("????????? ?????? TokenProvider - validateToken()", me);
		} catch (ExpiredJwtException ee) {
			log.error("????????? ?????? TokenProvider - validateToken()", ee);
		} catch (UnsupportedJwtException ue) {
			log.error("???????????? ?????? ?????? TokenProvider - validateToken()", ue);
		} catch (IllegalArgumentException ie) {
			log.error("???????????? ?????? TokenProvider - validateToken()", ie);
		} catch (NullPointerException ne) {
			log.error("???????????? ?????? ?????? TokenProvider - validateToken()", ne);
		}
		return false;
	}

	public ValidateTokenDTO requestCheckToken(HttpServletRequest request) {
		log.info("TokenProvider - requestCheckToken() ...");

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
			log.error("?????? ?????? ?????? ????????????. TokenProvider - requestCheckToken()");
		} catch (Exception e) {
			log.error("TokenProvider - requestCheckToken()", e);
		}
		return ValidateTokenDTO.builder().code(2).token("").build();
	}

	public boolean saveRefresh(ReIssuanceTokenDTO reIssuanceTokenDTO) {
		log.info("TokenProvider - saveRefresh() ...");
		try {
			TokenEntity tokenEntity = tokenRepository.save(TokenEntity.builder()
																		.email(reIssuanceTokenDTO.getEmail())
																		.refreshToken(reIssuanceTokenDTO.getRefreshToken())
																		.build());
			if(tokenEntity.getEmail() != null) return true;
		} catch (NullPointerException ne) {
			log.error("?????? ?????? ??????????????????. TokenProvider - saveRefresh()", ne);
		} catch (Exception e) {
			log.error("?????? ??? ?????? ?????? TokenProvider - saveRefresh()", e);
		}
		return false;
	}

	public boolean validateExistingToken(String token) {
		log.info("TokenProvider - validateExistingToken() ...");
		try {
			if(this.validateToken(token)) {
				String userPk = this.getUserPk(token);
				String existingToken = tokenRepository.findByEmail(userPk).getRefreshToken();
				if(existingToken.equals(token)) return true;
			}
		} catch (Exception e) {
			log.error("?????? ????????? ?????? ?????? ?????? TokenProvider - validateExistingToken()", e);
		}
		return false;
	}

	public boolean updateRefresh(ReIssuanceTokenDTO reIssuanceTokenDTO) {
		log.info("TokenProvider - updateRefresh() ...");
		try {
			Integer result = tokenRepository.updateToken(reIssuanceTokenDTO.getRefreshToken(), reIssuanceTokenDTO.getEmail());
			if(result > 0) return true;
		} catch (NullPointerException ne) {
			log.error("?????? ???????????? ??????????????????. TokenProvider - updateRefresh()", ne);
		} catch (Exception e) {
			log.error("SERVER ERROR TokenProvider - updateRefresh()", e);
		}
		return false;
	}
}
