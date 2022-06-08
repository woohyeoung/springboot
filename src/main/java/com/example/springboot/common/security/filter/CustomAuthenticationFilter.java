package com.example.springboot.common.security.filter;

import com.example.springboot.common.response.Payload;
import com.example.springboot.common.config.properties.TokenProperties;
import com.example.springboot.common.security.handler.CustomLoginFailureHandler;
import com.example.springboot.common.security.handler.ResponseHandler;
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
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationFilter.class);
	private final CustomAuthenticationManager authenticationManager;
	private final TokenProvider tokenProvider;
	private final TokenRepository tokenRepository;

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		logger.info("AuthenticationFilter - attemptAuthentication() ...");
		try {
			UserEntity user = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());

			return authenticationManager.authenticate(authenticationToken);
		} catch (IOException ie) {
			logger.error("유저 정보를 읽지 못했습니다. CustomAuthenticationFilter - attemptAuthentication()", ie);
			unsuccessfulAuthentication(request, response, ie);
		} catch (NullPointerException ne) {
			logger.error("받은 유저 정보가 비어 있습니다. CustomAuthenticationFilter - attemptAuthentication()", ne);
			unsuccessfulAuthentication(request, response, ne);
		} catch (AuthenticationException ae) {
			logger.error("자격 증명에 실패하였습니다. CustomAuthenticationFilter - attemptAuthentication()", ae);
		} catch (Exception e) {
			logger.error("SERVER ERROR CustomAuthenticationFilter - attemptAuthentication()", e);
			unsuccessfulAuthentication(request, response, e);
		}
		return null;
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request,
											HttpServletResponse response,
											FilterChain chain, Authentication authResult) throws ServletException {
		logger.info("AuthenticationFilter - successfulAuthentication() ...");

		try {
//			CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();
			String principal = String.valueOf(authResult.getPrincipal());
			TokenEntity tokenEntity = tokenRepository.findByEmail(principal);
			if (tokenEntity != null) {
				if (tokenProvider.validateToken(tokenEntity.getRefreshToken())) {
					logger.info("RefreshToken validate success - AccessToken issuance");
					String accessToken = tokenProvider.generateAccessToken(principal);

					response.addHeader(TokenProperties.HEADER_KEY_ACCESS, TokenProperties.SECRET_TYPE_ACCESS + accessToken);
				}
				else {
					logger.info("RefreshToken Expired - All Token issuance");
					FirstTimeTokenDTO firstTimeTokenDTO = tokenProvider.generateToken(principal);
					if(!tokenProvider.updateRefresh(firstTimeTokenDTO.getReIssuanceTokenDTO())) logger.warn("Token Set Update to Token Repository - Fail");

					response.addHeader(TokenProperties.HEADER_KEY_ACCESS, TokenProperties.SECRET_TYPE_ACCESS + firstTimeTokenDTO.getAccessToken());
					response.addHeader(TokenProperties.HEADER_KEY_REFRESH, TokenProperties.SECRET_TYPE_REFRESH + firstTimeTokenDTO.getReIssuanceTokenDTO().getRefreshToken());
				}
			}
			else {
				logger.info("First Login User - All Token issuance");
				FirstTimeTokenDTO firstTimeTokenDTO = tokenProvider.generateToken(principal);
				if(!tokenProvider.saveRefresh(firstTimeTokenDTO.getReIssuanceTokenDTO())) logger.warn("Token Set Save to Token Repository - Fail");

				response.addHeader(TokenProperties.HEADER_KEY_ACCESS, TokenProperties.SECRET_TYPE_ACCESS + firstTimeTokenDTO.getAccessToken());
				response.addHeader(TokenProperties.HEADER_KEY_REFRESH,TokenProperties.SECRET_TYPE_REFRESH + firstTimeTokenDTO.getReIssuanceTokenDTO().getRefreshToken());
			}
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.OK, Payload.SIGN_IN_OK));
		} catch (IOException ie) {
			logger.error("유저 정보를 읽지 못했습니다. CustomAuthenticationFilter - successfulAuthentication()", ie);
		} catch (NullPointerException ne) {
			logger.error("받은 유저 정보가 비어 있습니다. CustomAuthenticationFilter - successfulAuthentication()", ne);
		} catch (Exception e) {
			logger.error("SERVER ERROR CustomAuthenticationFilter - successfulAuthentication()", e);
		}
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request,
											  HttpServletResponse response,
											  AuthenticationException failed) throws ServletException {
		logger.info("AuthenticationFilter - unsuccessfulAuthentication() ...");

		try {
			String message = new CustomLoginFailureHandler().onAuthenticationFailure(failed);

			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.BAD_REQUEST, Payload.SIGN_IN_FAIL + message));
		} catch (IOException ie) {
			logger.error("전달받은 정보를 읽지 못했습니다. CustomAuthenticationFilter - unsuccessfulAuthentication()", ie);
		} catch (Exception e) {
			logger.error("SERVER ERROR CustomAuthenticationFilter - unsuccessfulAuthentication()", e);
		}
	}

	public void unsuccessfulAuthentication(HttpServletRequest request,
										   HttpServletResponse response,
										   Exception exception) {
		logger.info("AuthenticationFilter - unsuccessfulAuthentication() ...");

		try {
			SecurityContextHolder.clearContext();
			String message = new CustomLoginFailureHandler().onAuthenticationFailure(exception);

			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.BAD_REQUEST, Payload.SIGN_IN_FAIL + message));
		} catch (IOException ie) {
			logger.error("전달받은 정보를 읽지 못했습니다. CustomAuthenticationFilter - unsuccessfulAuthentication()", ie);
		} catch (Exception e) {
			logger.error("SERVER ERROR CustomAuthenticationFilter - unsuccessfulAuthentication()", e);
		}
	}
}
