package com.example.springboot.common.security.filter;

import com.example.springboot.common.security.jwt.TokenProvider;
import com.example.springboot.common.security.auth.CustomUserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

public class AuthorizationFilter extends BasicAuthenticationFilter {
	private static final Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);
	private final TokenProvider tokenProvider;
	private CustomUserDetailService userDetailService;

	@Autowired
	public AuthorizationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider, CustomUserDetailService userDetailService) {
		super(authenticationManager);
		this.tokenProvider = tokenProvider;
		this.userDetailService = userDetailService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		logger.info("AuthorizationFilter - doFilterInternal() ...");
		try {
			String token = tokenProvider.requestCheckToken(request);
			if(token == null) {
				filterChain.doFilter(request, response);
				return;
			}

			if(tokenProvider.validateToken(token)) {
				String userPk = tokenProvider.getUserPk(token);

				UserDetails userDetails = userDetailService.loadUserByUsername(userPk);
				UsernamePasswordAuthenticationToken authenticationToken =
						new UsernamePasswordAuthenticationToken(userDetails,
						null,
						userDetails.getAuthorities());
				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		} catch (Exception e) {
			logger.error("사용자 인증을 확인하지 못해 인가할 수 없습니다.", e);
		}
		filterChain.doFilter(request, response);
	}
}
