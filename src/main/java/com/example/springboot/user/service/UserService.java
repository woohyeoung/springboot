package com.example.springboot.user.service;

import com.example.springboot.common.security.jwt.TokenProvider;
import com.example.springboot.user.domain.token.TokenEntity;
import com.example.springboot.user.domain.token.TokenRepository;
import com.example.springboot.user.domain.user.UserEntity;
import com.example.springboot.user.domain.user.UserRepository;
import com.example.springboot.user.model.user.UserRequestDTO;
import com.example.springboot.user.model.user.UserSignInRequestDTO;
import com.example.springboot.user.model.user.UserSignInResponseDTO;
import com.example.springboot.user.model.user.UserSignUpRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final TokenRepository tokenRepository;

	@Autowired
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider, TokenRepository tokenRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
		this.tokenRepository = tokenRepository;
	}

	public boolean validateUser(String email) {
		return userRepository.existsByEmail(email);
	}

	public String join(UserSignUpRequestDTO sign) {
		logger.info("UserService join() ...");
		try {
			if(!validateUser(sign.getEmail())) {
				UserEntity entity = userRepository.save(UserEntity.builder()
						.email(sign.getEmail())
						.password(passwordEncoder.encode(sign.getPassword()))
						.name(sign.getName())
						.build());
				return entity.getEmail();
			} else  return "ERROR : 이미 존재하는 정보입니다.";
		} catch (DataIntegrityViolationException de) {
			de.printStackTrace();
			return "ERROR : 입력되지 않은 값이 있습니다.";
		} catch (Exception e) {
			e.printStackTrace();
			return "SERVER : ";
		}
	}

	public UserSignInResponseDTO login(UserSignInRequestDTO sign) {
		logger.info("UserService login() ...");
		try {
			if(!validateUser(sign.getEmail()))
				return UserSignInResponseDTO.builder()
											.key(0)
											.message("가입되지 않은 E-Mail 입니다.")
											.build();

			UserEntity user = userRepository.findByEmail(sign.getEmail());

			if(!passwordEncoder.matches(sign.getPassword(), user.getPassword()))
				return UserSignInResponseDTO.builder()
											.key(0)
											.message("잘못된 비밀번호 입니다.")
											.build();

			return UserSignInResponseDTO.builder()
										.key(2)
										.message("Success")
										.build();
		} catch (IllegalArgumentException ie) {
			ie.printStackTrace();
			return UserSignInResponseDTO.builder()
										.key(0)
										.message("입력 형식에 맞지 않게 기입하셨습니다.")
										.build();
		} catch (Exception e) {
			e.printStackTrace();
			return UserSignInResponseDTO.builder()
										.key(1)
										.message("SERVER ERROR")
										.build();
		}
	}

	public UserSignInResponseDTO logout(UserRequestDTO userRequestDTO) {
		logger.info("UserService logout() ...");
		UserEntity user = userRepository.findByEmail(userRequestDTO.getEmail());

		if(user == null) return UserSignInResponseDTO.builder()
														.key(0)
														.message("사용자가 존재하지 않습니다.")
														.build();
		try {
			TokenEntity tokenEntity = tokenRepository.findByEmail(userRequestDTO.getEmail());
			if(!tokenProvider.validateToken(tokenEntity.getAccessToken()))
				return UserSignInResponseDTO.builder()
											.key(0)
											.message("잘못된 요청입니다.")
											.build();

			if(tokenEntity.getAccessToken() != null)
				if(tokenRepository.updateByAccessToken(null) != null) logger.warn("Delete AccessToken - Fail");
			return UserSignInResponseDTO.builder()
										.key(2)
										.message("Success")
										.build();
		} catch (Exception e) {
			e.printStackTrace();
			return UserSignInResponseDTO.builder()
										.key(1)
										.message("SERVER : ")
										.build();
		}
	}
}
