package com.example.springboot.common.security.handler;

import com.example.springboot.user.domain.user.UserEntity;
import com.example.springboot.user.domain.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptHandler {
	private static final Logger logger = LoggerFactory.getLogger(BcryptHandler.class);
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final UserRepository userRepository;

	@Autowired
	public BcryptHandler(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public String passwordEncode(String password) {
		return passwordEncoder.encode(password);
	}

	public boolean emailValid(String email) {
		logger.info("BcryptHandler - emailValid() ...");
		try {
			UserEntity userEntity = userRepository.findByEmail(email);
			if (userEntity != null) {
				logger.info("UserEntity Validate - Success");
				if(userEntity.getEmail() != null) {
					logger.info("User Email Validate - Success");
					return true;
				} else logger.warn("User Email Validate - Fail");
			} else logger.warn("UserEntity Validate - Fail");
		} catch (Exception e) {
			logger.error("SERVER ERROR BcryptHandler - emailValid()", e);
		}
		return false;
	}

	public boolean passwordValid(String email, String password) {
		logger.info("BcryptHandler - passwordValid() ...");
		try {
			UserEntity userEntity = userRepository.findByEmail(email);
			if (passwordEncoder.matches(password, userEntity.getPassword())) {
				logger.info("Password validate - Success");
				return true;
			} else logger.warn("Password validate - Fail");
		} catch (Exception e) {
			logger.error("SERVER ERROR BcryptHandler - passwordValid()", e);
		}
		return false;
	}
}
