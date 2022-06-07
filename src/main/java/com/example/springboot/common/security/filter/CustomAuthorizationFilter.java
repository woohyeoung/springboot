package com.example.springboot.common.security.filter;

import com.example.springboot.common.config.properties.TokenProperties;
import com.example.springboot.common.response.Payload;
import com.example.springboot.common.security.handler.ResponseHandler;
import com.example.springboot.common.security.jwt.TokenProvider;
import com.example.springboot.common.security.auth.CustomUserDetailService;
import com.example.springboot.user.model.token.ValidateTokenDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthorizationFilter extends BasicAuthenticationFilter {
	private static final Logger logger = LoggerFactory.getLogger(CustomAuthorizationFilter.class);
	private final TokenProvider tokenProvider;
	private final CustomUserDetailService userDetailService;

	public CustomAuthorizationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider, CustomUserDetailService userDetailService) {
		super(authenticationManager);
		this.tokenProvider = tokenProvider;
		this.userDetailService = userDetailService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		logger.info("AuthorizationFilter - doFilterInternal() ...");
		try {
			ValidateTokenDTO validateTokenDTO = tokenProvider.requestCheckToken(request);
			String token = validateTokenDTO.getToken();
			switch (validateTokenDTO.getCode()) {
				case 0 :
					if (tokenProvider.validateToken(token)) {
						logger.info("Access Token Validation - Success");

						String userPk = tokenProvider.getUserPk(token);

						UserDetails userDetails = userDetailService.loadUserByUsername(userPk);
						UsernamePasswordAuthenticationToken authenticationToken =
								new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

						authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					} else {
						logger.info("Access Token Validation - Fail");

//						response.setContentType("text/html; charset=UTF-8");
//						response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.BAD_REQUEST, Payload.SIGN_IN_FAIL));
					}
					filterChain.doFilter(request, response);
					return;
				case 1 :
					if (tokenProvider.validateExistingToken(token)) {
						logger.info("Refresh Token Validation - Success");
						String accessToken = tokenProvider.generateAccessToken(tokenProvider.getUserPk(token));

						response.addHeader(TokenProperties.HEADER_KEY_ACCESS, TokenProperties.SECRET_TYPE_ACCESS + accessToken);

//						response.setContentType("text/html; charset=UTF-8");
//						response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.OK, Payload.SIGN_IN_OK));
					} else {
						logger.info("Refresh Token Validation - Fail");

//						response.setContentType("text/html; charset=UTF-8");
//						response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.BAD_REQUEST, Payload.SIGN_IN_FAIL));
					}
					filterChain.doFilter(request, response);
					return;
				case 2 :
				default:
					logger.warn("Access/Refresh Token Validation - Fail");
			}
		} catch (NullPointerException ne) {
			logger.error("토큰 값이 비어있습니다. CustomAuthorizationFilter - doFilterInternal()");
		} catch (Exception e) {
			logger.error("사용자 인증을 확인하지 못해 인가할 수 없습니다. CustomAuthorizationFilter - doFilterInternal()", e);
		}
//		response.setContentType("text/html; charset=UTF-8");
//		response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.INTERNAL_SERVER_ERROR, Payload.SIGN_IN_FAIL));

		filterChain.doFilter(request, response);
	}
}
