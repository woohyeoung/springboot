package com.example.springboot.common.security.filter;

import com.example.springboot.common.response.Payload;
import com.example.springboot.common.response.ResponseDTO;
import com.example.springboot.common.security.auth.CustomUserDetails;
import com.example.springboot.common.security.jwt.TokenProperties;
import com.example.springboot.common.security.jwt.TokenProvider;
import com.example.springboot.user.domain.token.TokenEntity;
import com.example.springboot.user.domain.token.TokenRepository;
import com.example.springboot.user.domain.user.UserEntity;
import com.example.springboot.user.model.token.FirstTimeTokenDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		logger.info("AuthenticationFilter - attemptAuthentication() ...");
		try {
			objectMapper = new ObjectMapper();
			UserEntity user = objectMapper.readValue(request.getInputStream(), UserEntity.class);

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

			return authenticationManager.authenticate(authenticationToken);
		} catch (IOException ie) {
			logger.error("유저 정보를 읽지 못했습니다. AuthenticationFilter - attemptAuthentication()", ie);
		} catch (UsernameNotFoundException ue) {
			logger.error("받은 유저의 정보와 일치하는 유저가 존재하지 않습니다. AuthenticationFilter - attemptAuthentication()", ue);
		} catch (NullPointerException ne) {
			logger.error("받은 유저 정보가 비어 있습니다. AuthenticationFilter - attemptAuthentication()", ne);
		} catch (AuthenticationException ae) {
			logger.error("자격 증명에 실패하였습니다. AuthenticationFilter - attemptAuthentication()", ae);
		} catch (Exception e) {
			logger.error("SERVER ERROR AuthenticationFilter - attemptAuthentication()", e);
		}
		return null;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
											HttpServletResponse response,
											FilterChain chain, Authentication authResult) throws ServletException {
		logger.info("AuthenticationFilter - successfulAuthentication() ...");

		try {
			CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
			TokenEntity tokenEntity = tokenRepository.findByEmail(userDetails.getUsername());
			if(tokenEntity != null) {
				logger.info("Existing Refresh Token Check - Success");
				String accessToken = tokenProvider.generateAccessToken(userDetails.getUsername());

				response.addHeader(TokenProperties.HEADER_KEY_ACCESS, TokenProperties.SECRET_TYPE_ACCESS + accessToken);

				String result = objectMapper.writeValueAsString(ResponseDTO.builder()
																			.status(HttpStatus.OK)
																			.message(Payload.SIGN_IN_OK)
																			.build());

				response.getWriter().write(result);
			}
			else {
				FirstTimeTokenDTO firstTimeTokenDTO = tokenProvider.generateToken(userDetails.getUsername());
				if(!tokenProvider.saveRefresh(firstTimeTokenDTO.getReIssuanceTokenDTO())) logger.warn("Token Set Save to Token Repository - Fail");

				response.addHeader(TokenProperties.HEADER_KEY_ACCESS, TokenProperties.SECRET_TYPE_ACCESS + firstTimeTokenDTO.getAccessToken());
				response.addHeader(TokenProperties.HEADER_KEY_REFRESH,TokenProperties.SECRET_TYPE_REFRESH + firstTimeTokenDTO.getReIssuanceTokenDTO().getRefreshToken());

				String result = objectMapper.writeValueAsString(ResponseDTO.builder()
																			.status(HttpStatus.OK)
																			.message(Payload.SIGN_IN_OK)
																			.build());
				response.getWriter().write(result);
			}
		} catch (IOException ie) {
			logger.error("유저 정보를 읽지 못했습니다. AuthenticationFilter - successfulAuthentication()", ie);
		} catch (NullPointerException ne) {
			logger.error("받은 유저 정보가 비어 있습니다. AuthenticationFilter - successfulAuthentication()", ne);
		} catch (Exception e) {
			logger.error("SERVER ERROR AuthenticationFilter - successfulAuthentication()", e);
		}
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
											  HttpServletResponse response,
											  AuthenticationException failed) throws ServletException {
		System.out.println("오니?");
		logger.info("AuthenticationFilter - unsuccessfulAuthentication() ...");
		try {
			String result = objectMapper.writeValueAsString(ResponseDTO.builder()
																		.status(HttpStatus.BAD_REQUEST)
																		.message(Payload.SIGN_IN_FAIL)
																		.build());

			response.getWriter().write(result);
		} catch (IOException ie) {
			logger.error("전달받은 정보를 읽지 못했습니다. AuthenticationFilter - unsuccessfulAuthentication()", ie);
		} catch (Exception e) {
			logger.error("SERVER ERROR", e);
		}
	}
}