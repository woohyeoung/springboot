package com.example.springboot.common.security.handler;

import com.example.springboot.common.config.properties.ConfigProperties;
import com.example.springboot.common.config.properties.RoleProperties;
import com.example.springboot.common.config.properties.TokenProperties;
import com.example.springboot.common.response.Payload;
import com.example.springboot.common.security.jwt.TokenProvider;
import com.example.springboot.user.model.token.ValidateTokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class RoleInterceptorHandler implements HandlerInterceptor {

	private final BcryptHandler bcryptHandler;
	private final TokenProvider tokenProvider;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		log.info("RoleInterceptorHandler - preHandle() ...");
		boolean result = false;
		try {
			ValidateTokenDTO validateTokenDTO = tokenProvider.requestCheckToken(request);
			String token = validateTokenDTO.getToken();
			Outer:
			{
				if (tokenProvider.validateToken(token)) {
					log.info("Token validate - success");
					String email = tokenProvider.getUserPk(token);

					if (bcryptHandler.emailValid(email)) {
						log.info("User validate - Success");
						String role = bcryptHandler.roleValid(email);

						if (request.getRequestURI().startsWith(ConfigProperties.ADMIN_ROLE)) {
							log.info("ADMIN role validate ...");
							if (role != null && role.equals(RoleProperties.ADMIN_ROLE)) {
								log.info("ADMIN role validate - Success");
								result = true;
							} else {
								log.warn("ADMIN role validate - Fail");
								response.setContentType("text/html; charset=UTF-8");
								response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.BAD_REQUEST, Payload.USER_ROLE_CHECK_FAIL));
							}
							break Outer;
						}
						if (request.getRequestURI().startsWith(ConfigProperties.USER_ROLE)) {
							log.info("USER role validate ...");
							if (role != null && role.equals(RoleProperties.USER_ROLE)) {
								log.info("USER role validate - Success");
								result = true;
							} else {
								log.warn("USER role validate - Fail");
								response.setContentType("text/html; charset=UTF-8");
								response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.BAD_REQUEST, Payload.USER_ROLE_CHECK_FAIL));
							}
							break Outer;
						}
						log.warn("Unverified role ACCESS ... ");
						result = true;
					} else {
						log.warn("Request User is not exist RoleInterceptorHandler - preHandle()");
						response.setContentType("text/html; charset=UTF-8");
						response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.BAD_REQUEST, Payload.USER_ROLE_CHECK_FAIL));
					}
				} else {
					log.warn("Token validate - Fail");
					response.setContentType("text/html; charset=UTF-8");
					response.getWriter().write(new ResponseHandler().convertResult(HttpStatus.BAD_REQUEST, Payload.TOKEN_FAIL));
				}
			}
			return result;
		} catch (IOException ie) {
			log.error("역할이 입력되지 않았습니다. RoleInterceptorHandler - preHandle()", ie);
		} catch (NullPointerException ne) {
			log.error("역할이 존재하지 않습니다. RoleInterceptorHandler - preHandle()", ne);
		} catch (Exception e) {
			log.error("SERVER ERROR RoleInterceptorHandler - preHandle()", e);
		}
		return false;
	}
}
