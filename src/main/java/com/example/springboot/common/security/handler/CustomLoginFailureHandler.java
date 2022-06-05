package com.example.springboot.common.security.handler;

import com.example.springboot.common.response.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

@Service("loginFailureHandler")
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {
	private static final Logger logger = LoggerFactory.getLogger(CustomLoginFailureHandler.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		logger.info("CustomLoginHandler - onAuthenticationFailure() ...");

		String message = "SERVER ERROR";
		try {
			if(exception instanceof LockedException) message = "잠겨 있는 계정";
			else if(exception instanceof DisabledException) message = "비활성화 계정";
			else if(exception instanceof CredentialsExpiredException) message = "비밀번호가 만료된 계정";
			else if(exception instanceof AccountExpiredException) message = "기간이 만료된 계정";
			else if(exception instanceof BadCredentialsException) message = "기간이 만료된 계정";
			else if(exception instanceof UsernameNotFoundException) message = "계정 자격 증명 실패";
			else message = "알 수 없는 이유";
		} catch (Exception e) {
			logger.error("SERVER ERROR CustomLoginHandler - onAuthenticationFailure", e);
		}
		String result = objectMapper.writeValueAsString(ResponseDTO.builder()
																	.status(HttpStatus.OK)
																	.message(message)
																	.build());
		result= URLDecoder.decode(result,"UTF-8");
		response.getWriter().write(result);
	}
}
