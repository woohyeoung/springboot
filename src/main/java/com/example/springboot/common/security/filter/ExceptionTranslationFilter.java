package com.example.springboot.common.security.filter;

import io.jsonwebtoken.lang.Assert;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class ExceptionTranslationFilter extends GenericFilterBean {

	private AccessDeniedHandler accessDeniedHandler = new AccessDeniedHandlerImpl();
	private AuthenticationEntryPoint authenticationEntryPoint;
	private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();
	private RequestCache requestCache = new HttpSessionRequestCache();
	private final MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	public ExceptionTranslationFilter(AuthenticationEntryPoint authenticationEntryPoint) {
		this(authenticationEntryPoint, new HttpSessionRequestCache());
	}

	public ExceptionTranslationFilter(AuthenticationEntryPoint authenticationEntryPoint, RequestCache requestCache) {
		Assert.notNull(authenticationEntryPoint, "AuthenticationEntryPoint can not be null");
		Assert.notNull(requestCache, "RequestCache can not be null");
		this.authenticationEntryPoint = authenticationEntryPoint;
		this.requestCache = requestCache;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

	}
}
