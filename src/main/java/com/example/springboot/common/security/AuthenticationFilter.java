package com.example.springboot.common.security;

import com.example.springboot.user.domain.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
public class AuthenticationFilter extends GenericFilterBean {

	private final TokenProvider tokenProvider;
	private final TokenRepository tokenRepository;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		String token = tokenProvider.resolveToken((HttpServletRequest) request);

		if(token != null && tokenProvider.validateToken(token)) {
			String isSignOut = String.valueOf(tokenRepository.findByEmail(tokenProvider.getAuthentication(token).getName()));
			if(ObjectUtils.isEmpty(isSignOut)) {
				Authentication authentication = tokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		chain.doFilter(request, response);
	}

}
