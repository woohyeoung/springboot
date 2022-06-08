package com.example.springboot.user.service;

import com.example.springboot.common.security.handler.BcryptHandler;
import com.example.springboot.user.domain.user.UserEntity;
import com.example.springboot.user.domain.user.UserRepository;
import com.example.springboot.user.model.user.UserSignResponseDTO;
import com.example.springboot.user.model.user.UserSignUpRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
	private final UserRepository userRepository;
	private final BcryptHandler bcryptHandler;

	@Autowired
	public UserService(UserRepository userRepository, BcryptHandler bcryptHandler) {
		this.userRepository = userRepository;
		this.bcryptHandler = bcryptHandler;
	}

	public UserSignResponseDTO join(UserSignUpRequestDTO sign) {
		log.info("UserService join() ...");
		try {
			if(!bcryptHandler.emailValid(sign.getEmail())) {
				UserEntity entity = userRepository.save(sign.toEntity(bcryptHandler.passwordEncode(sign.getPassword())));
				if(entity.getEmail() != null) return UserSignResponseDTO.builder().key(2).message("Success").build();

				return UserSignResponseDTO.builder().key(0).message("회원가입이 정상적으로 이루어지지 않았습니다.").build();
			}
			return UserSignResponseDTO.builder().key(0).message("이미 존재하는 정보입니다.").build();
		} catch (DataIntegrityViolationException de) {
			log.error("입력되지 않은 값이 있습니다.", de);
		} catch (Exception e) {
			log.error("UserService Exception join()", e);
		}
		return UserSignResponseDTO.builder().key(1).message("SERVER ERROR").build();
	}
}
