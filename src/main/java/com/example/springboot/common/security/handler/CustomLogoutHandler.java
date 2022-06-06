package com.example.springboot.common.security.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class CustomLogoutHandler implements LogoutHandler {
	private static final Logger logger = LoggerFactory.getLogger(CustomLogoutHandler.class);

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		try {
			logger.info("CustomLogoutHandler - logout() ...");

		} catch (Exception e) {
			logger.error("SERVER ERROR CustomLogoutHandler - logout()", e);
		}
	}
}
