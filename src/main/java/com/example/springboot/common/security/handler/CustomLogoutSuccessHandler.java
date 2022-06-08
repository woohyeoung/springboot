package com.example.springboot.common.security.handler;

import com.example.springboot.common.response.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
	private static final Logger logger = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		logger.info("CustomLogoutSuccessHandler - onLogoutSuccess() ...");
		try {
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.OK, Payload.SIGN_OUT_OK));
			return;
		} catch (IOException ie) {
			logger.error("전달받은 정보를 읽지 못했습니다. CustomLogoutSuccessHandler - onLogoutSuccess()", ie);
		} catch (Exception e) {
			logger.error("SERVER ERROR CustomLogoutSuccessHandler - onLogoutSuccess()", e);
		}

		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.INTERNAL_SERVER_ERROR, Payload.SIGN_OUT_FAIL));
	}
}
