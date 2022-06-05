package com.example.springboot.user.service;

import com.example.springboot.common.security.auth.BcryptHandler;
import com.example.springboot.user.domain.user.UserEntity;
import com.example.springboot.user.domain.user.UserRepository;
import com.example.springboot.user.model.user.UserRequestDTO;
import com.example.springboot.user.model.user.UserSignResponseDTO;
import com.example.springboot.user.model.user.UserSignUpRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	private final UserRepository userRepository;
	private final BcryptHandler bcryptHandler;

	@Autowired
	public UserService(UserRepository userRepository, BcryptHandler bcryptHandler) {
		this.userRepository = userRepository;
		this.bcryptHandler = bcryptHandler;
	}

	public UserSignResponseDTO join(UserSignUpRequestDTO sign) {
		logger.info("UserService join() ...");
		try {
			if(userRepository.existsByEmail(sign.getEmail())) {
				UserEntity entity = userRepository.save(sign.toEntity(bcryptHandler.passwordEncode(sign.getPassword())));
				if(entity.getEmail() != null) return UserSignResponseDTO.builder().key(2).message("Success").build();

				return UserSignResponseDTO.builder().key(0).message("회원가입이 정상적으로 이루어지지 않았습니다.").build();
			}
			return UserSignResponseDTO.builder().key(0).message("이미 존재하는 정보입니다.").build();
		} catch (DataIntegrityViolationException de) {
			logger.error("입력되지 않은 값이 있습니다.", de);
		} catch (Exception e) {
			logger.error("UserService Exception join()", e);
		}
		return UserSignResponseDTO.builder().key(1).message("SERVER ERROR").build();
	}

//	public UserSignResponseDTO login(UserSignInRequestDTO sign) {
//		logger.info("UserService login() ...");
//		try {
//			if(sign.getEmail() != null) return UserSignResponseDTO.builder().key(2).message("Success").build();
//		} catch (Exception e) {
//			logger.error("UserService Exception login()", e);
//		}
//		return UserSignResponseDTO.builder().key(1).message("SERVER ERROR").build();
//	}

	public UserSignResponseDTO logout(UserRequestDTO userRequestDTO) {
		logger.info("UserService logout() ...");
		try {
			UserEntity user = userRepository.findByEmail(userRequestDTO.getEmail());

			if(user == null) return UserSignResponseDTO.builder()
															.key(0)
															.message("사용자가 존재하지 않습니다.")
															.build();

			return UserSignResponseDTO.builder().key(2).message("Success").build();
		} catch (Exception e) {
			logger.error("UserService Exception logout()", e);
		}
		return UserSignResponseDTO.builder().key(1).message("SERVER ERROR").build();
	}
}
