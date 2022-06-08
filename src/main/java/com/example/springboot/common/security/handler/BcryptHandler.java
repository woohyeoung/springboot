package com.example.springboot.common.security.handler;

import com.example.springboot.user.domain.user.UserEntity;
import com.example.springboot.user.domain.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BcryptHandler {
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final UserRepository userRepository;

	@Autowired
	public BcryptHandler(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public String passwordEncode(String password) {
		return passwordEncoder.encode(password);
	}

	public String roleValid(String email) {
		log.info("BcryptHandler - roleValid() ...");
		if(userRepository.existsByEmail(email)) {
			log.info("User Email Validate - Success");
			UserEntity userEntity = userRepository.findByEmail(email);

			return userEntity.getRole();
		}
		log.warn("User Email Validate - Fail");
		return null;
	}

	public boolean emailValid(String email) {
		log.info("BcryptHandler - emailValid() ...");
		try {
			UserEntity userEntity = userRepository.findByEmail(email);
			if (userEntity != null) {
				log.info("UserEntity Validate - Success");
				if(userEntity.getEmail() != null) {
					log.info("User Email Validate - Success");
					return true;
				} else log.warn("User Email Validate - Fail");
			} else log.warn("UserEntity Validate - Fail");
		} catch (Exception e) {
			log.error("SERVER ERROR BcryptHandler - emailValid()", e);
		}
		return false;
	}

	public boolean passwordValid(String email, String password) {
		log.info("BcryptHandler - passwordValid() ...");
		try {
			UserEntity userEntity = userRepository.findByEmail(email);
			if (passwordEncoder.matches(password, userEntity.getPassword())) {
				log.info("Password validate - Success");
				return true;
			} else log.warn("Password validate - Fail");
		} catch (Exception e) {
			log.error("SERVER ERROR BcryptHandler - passwordValid()", e);
		}
		return false;
	}
}
