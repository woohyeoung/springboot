package com.example.springboot.common.security.filter;

import com.example.springboot.common.security.handler.BcryptHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {
	private final BcryptHandler bcryptHandler;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		log.info("CustomAuthenticationProvider - authenticate() ...");
		try {
			String email = String.valueOf(authentication.getPrincipal());
			String password = String.valueOf(authentication.getCredentials());

			if(email == null || email.equals("")) {
				log.warn("User Email validate - Empty or null");
				throw new NullPointerException();
			}
			else if(password == null || password.equals("")) {
				log.warn("User Password validate - Empty or null");
				throw new NullPointerException();
			}
			else {
				if(bcryptHandler.emailValid(email)) {
					if (bcryptHandler.passwordValid(email, password))
						return new UsernamePasswordAuthenticationToken(email, password);
				}
			}
		} catch (LockedException le) {
			log.error("잠겨 있는 계정 CustomAuthenticationManager - authenticate()", le);
		} catch (DisabledException de) {
			log.error("비활성화 계정 CustomAuthenticationManager - authenticate()", de);
		} catch (CredentialsExpiredException ce) {
			log.error("비밀번호가 만료된 계정 CustomAuthenticationManager - authenticate()", ce);
		} catch (AccountExpiredException ae) {
			log.error("기간이 만료된 계정 CustomAuthenticationManager - authenticate()", ae);
		} catch (BadCredentialsException be) {
			log.error("계정 자격 증명 실패 CustomAuthenticationManager - authenticate()", be);
		} catch (UsernameNotFoundException ue) {
			log.error("존재하지 않는 계정 CustomAuthenticationManager - authenticate()", ue);
		} catch (Exception e) {
			log.error("SERVER ERROR CustomAuthenticationManager - authenticate()", e);
		}
		throw new NullPointerException();
	}
}
