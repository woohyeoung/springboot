package com.example.springboot.user.service;

import com.example.springboot.common.security.jwt.TokenProvider;
import com.example.springboot.user.domain.token.TokenRepository;
import com.example.springboot.user.domain.user.UserEntity;
import com.example.springboot.user.domain.user.UserRepository;
import com.example.springboot.user.model.user.UserSignInRequestDTO;
import com.example.springboot.user.model.user.UserSignInResponseDTO;
import com.example.springboot.user.model.user.UserSignUpRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

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
		try {
			if(!validateUser(sign.getEmail()))
				return new UserSignInResponseDTO(0, "가입되지 않은 E-Mail 입니다.");

			UserEntity user = userRepository.findByEmail(sign.getEmail());

			if(!passwordEncoder.matches(sign.getPassword(), user.getPassword()))
				return new UserSignInResponseDTO(0, "잘못된 비밀번호 입니다.");

			return new UserSignInResponseDTO(2, "success");
		} catch (IllegalArgumentException ie) {
			ie.printStackTrace();
			return new UserSignInResponseDTO(0, "입력 형식에 맞지 않게 기입하셨습니다.");
		} catch (Exception e) {
			e.printStackTrace();
			return new UserSignInResponseDTO(1, "SERVER ERROR");
		}
	}

	public String logout(String token) {
		if(token == null) return "ERROR : 토큰이 존재하지 않습니다.";
		try {
			if(!tokenProvider.validateToken(token))
				return "ERROR : 잘못된 요청입니다.";

			Authentication authentication = tokenProvider.getAuthentication(token);
			String result = tokenRepository.findByEmail(authentication.getName()).getEmail();
			if(result != null)
				tokenRepository.deleteByAccessToken(result);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "SERVER : ";
		}
	}
}
