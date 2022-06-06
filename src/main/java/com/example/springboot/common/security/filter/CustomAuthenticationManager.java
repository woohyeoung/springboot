package com.example.springboot.common.security.filter;

import com.example.springboot.common.security.handler.BcryptHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {
	private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationManager.class);
	private final BcryptHandler bcryptHandler;

	@Autowired
	public CustomAuthenticationManager(BcryptHandler bcryptHandler) {
		this.bcryptHandler = bcryptHandler;
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		logger.info("CustomAuthenticationProvider - authenticate() ...");
		try {
			String email = String.valueOf(authentication.getPrincipal());
			String password = String.valueOf(authentication.getCredentials());

			if(email == null || email.equals("")) {
				logger.warn("User Email validate - Empty or null");
				return null;
			}
			else if(password == null || password.equals("")) {
				logger.warn("User Password validate - Empty or null");
				return null;
			}
			else {
				if(bcryptHandler.emailValid(email)) {
					if (bcryptHandler.passwordValid(email, password))
						return new UsernamePasswordAuthenticationToken(email, password);
				}
			}
		} catch (LockedException le) {
			logger.error("잠겨 있는 계정 CustomAuthenticationManager - authenticate()", le);
		} catch (DisabledException de) {
			logger.error("비활성화 계정 CustomAuthenticationManager - authenticate()", de);
		} catch (CredentialsExpiredException ce) {
			logger.error("비밀번호가 만료된 계정 CustomAuthenticationManager - authenticate()", ce);
		} catch (AccountExpiredException ae) {
			logger.error("기간이 만료된 계정 CustomAuthenticationManager - authenticate()", ae);
		} catch (BadCredentialsException be) {
			logger.error("계정 자격 증명 실패 CustomAuthenticationManager - authenticate()", be);
		} catch (UsernameNotFoundException ue) {
			logger.error("존재하지 않는 계정 CustomAuthenticationManager - authenticate()", ue);
		} catch (Exception e) {
			logger.error("SERVER ERROR CustomAuthenticationManager - authenticate()", e);
		}
		return null;
	}
}
