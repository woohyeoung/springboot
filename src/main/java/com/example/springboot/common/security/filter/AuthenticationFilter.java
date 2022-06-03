package com.example.springboot.common.security.filter;

import com.example.springboot.common.security.auth.CustomUserDetails;
import com.example.springboot.common.security.jwt.TokenProperties;
import com.example.springboot.common.security.jwt.TokenProvider;
import com.example.springboot.user.domain.token.TokenEntity;
import com.example.springboot.user.domain.token.TokenRepository;
import com.example.springboot.user.domain.user.UserEntity;
import com.example.springboot.user.model.token.FirstTimeTokenDTO;
import com.example.springboot.user.model.user.UserSignResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
	private final AuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;
	private final TokenRepository tokenRepository;
	private ObjectMapper objectMapper;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		logger.info("AuthenticationFilter - attemptAuthentication() ...");
		try {
			objectMapper = new ObjectMapper();
			UserEntity user = objectMapper.readValue(request.getInputStream(), UserEntity.class);

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

			return authenticationManager.authenticate(authenticationToken);
		} catch (IOException ie) {
			logger.error("값을 읽지 못했습니다. AuthenticationFilter - attemptAuthentication()", ie);
		} catch (UsernameNotFoundException ue) {
			logger.error("유저가 존재하지 않습니다. AuthenticationFilter - attemptAuthentication()", ue);
		}
		return null;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
											HttpServletResponse response,
											FilterChain chain, Authentication authResult) throws IOException, ServletException {
		logger.info("AuthenticationFilter - successfulAuthentication() ...");

		CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
		TokenEntity tokenEntity = tokenRepository.findByEmail(userDetails.getUsername());

		if(tokenEntity != null) {
			String accessToken = tokenProvider.generateAccessToken(userDetails.getUsername());

			if (!tokenProvider.changeAccessToken(accessToken)) logger.warn("Access Token Save - Fail");

			response.addHeader(TokenProperties.HEADER_KEY_ACCESS, TokenProperties.SECRET_TYPE_ACCESS + accessToken);
			return;
		}
		FirstTimeTokenDTO firstTimeTokenDTO = tokenProvider.generateToken(userDetails.getUsername());

		if(!tokenProvider.saveRefresh(firstTimeTokenDTO)) logger.warn("Token Set Save - Fail");

		response.addHeader(TokenProperties.HEADER_KEY_ACCESS, TokenProperties.SECRET_TYPE_ACCESS + firstTimeTokenDTO.getAccessToken());
		response.addHeader(TokenProperties.HEADER_KEY_REFRESH,TokenProperties.SECRET_TYPE_REFRESH + firstTimeTokenDTO.getReIssuanceTokenDTO().getRefreshToken());
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
											  HttpServletResponse response,
											  AuthenticationException failed) throws IOException, ServletException {
		logger.info("AuthenticationFilter - unsuccessfulAuthentication() ...");

		objectMapper = new ObjectMapper();
		UserEntity user = objectMapper.readValue(request.getInputStream(), UserEntity.class);

		response.setContentType("application/json");
		response.setCharacterEncoding("utf-8");

		UserSignResponseDTO sign = UserSignResponseDTO.builder()
														.email(user.getEmail())
														.message("로그인 실패하였습니다. attemptAuthentication() - Fail")
														.build();

		objectMapper = new ObjectMapper();

		String result = objectMapper.writeValueAsString(sign);
		response.getWriter().write(result);
	}
}
