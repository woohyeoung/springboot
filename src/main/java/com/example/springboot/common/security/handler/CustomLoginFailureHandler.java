package com.example.springboot.common.security.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomLoginFailureHandler {
	private static final Logger logger = LoggerFactory.getLogger(CustomLoginFailureHandler.class);

	public String onAuthenticationFailure(Exception exception) {
		logger.info("CustomLoginFailureHandler - onAuthenticationFailure() ...");

		String message = "SERVER ERROR";
		try {
			if(exception instanceof LockedException) message = "잠겨 있는 계정";
			else if(exception instanceof DisabledException) message = "비활성화 계정";
			else if(exception instanceof CredentialsExpiredException) message = "비밀번호가 만료된 계정";
			else if(exception instanceof AccountExpiredException) message = "기간이 만료된 계정";
			else if(exception instanceof BadCredentialsException) message = "기간이 만료된 계정";
			else if(exception instanceof UsernameNotFoundException) message = "계정 자격 증명 실패";
			else if(exception instanceof NullPointerException) message = "아이디 또는 비밀번호 미입력";
			else message = "알 수 없는 이유";
		} catch (Exception e) {
			logger.error("SERVER ERROR CustomLoginFailureHandler - onAuthenticationFailure()", e);
		}
		return message;
	}
}
