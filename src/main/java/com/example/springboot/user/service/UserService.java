package com.example.springboot.user.service;

import com.example.springboot.common.security.TokenProvider;
import com.example.springboot.user.domain.token.TokenEntity;
import com.example.springboot.user.domain.token.TokenRepository;
import com.example.springboot.user.domain.user.UserEntity;
import com.example.springboot.user.domain.user.UserRepository;
import com.example.springboot.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final TokenRepository tokenRepository;

	@Autowired
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider,
					   AuthenticationManagerBuilder authenticationManagerBuilder, TokenRepository tokenRepository) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
		this.authenticationManagerBuilder = authenticationManagerBuilder;
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
			UsernamePasswordAuthenticationToken authenticationToken = sign.toAuthentication();

			Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

			AccessResponseDTO accessResponseDTO = tokenProvider.generateToken(user.getEmail());

			TokenEntity.builder()
					.email(authentication.getName())
					.refreshToken(accessResponseDTO.getTokenResponseDTO().getRefreshToken())
					.refreshTime(accessResponseDTO.getTokenResponseDTO().getRTExp())
					.build();

			return new UserSignInResponseDTO(2, accessResponseDTO);
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
			TokenEntity result = tokenRepository.findByEmail(authentication.getName());
			if(result.getEmail() != null)
				tokenRepository.delete(result);
			return "Success";
		} catch (Exception e) {
			e.printStackTrace();
			return "SERVER : ";
		}
	}

	public UserSignInResponseDTO reIssuance(String token) {
		if(token == null) return new UserSignInResponseDTO(0, "토큰이 존재하지 않습니다.");
		try {
			if(!tokenProvider.validateToken(token))
				return new UserSignInResponseDTO(0, "잘못된 요청입니다.");

			Authentication authentication = tokenProvider.getAuthentication(token);

			String refresh = tokenRepository.findByEmail(authentication.getName()).getRefreshToken();

			if(refresh == null) return new UserSignInResponseDTO(0, "토큰이 존재하지 않습니다.");
			if(!tokenProvider.validateToken(refresh)) return new UserSignInResponseDTO(0, "토큰 정보가 유효하지 않습니다.");
			if(ObjectUtils.isEmpty(refresh)) return new UserSignInResponseDTO(0, "토큰이 만료되었습니다.");

			TokenEntity tokenEntity = tokenRepository.findByEmail(authentication.getName());
			String access = tokenProvider.generateAccessToken(authentication.getName());
			AccessResponseDTO accessResponseDTO = new AccessResponseDTO(access, TokenResponseDTO.builder()
																								.email(tokenEntity.getEmail())
																								.refreshToken(tokenEntity.getRefreshToken())
																								.RTExp(tokenEntity.getRefreshTime())
																								.build());

			return new UserSignInResponseDTO(2, accessResponseDTO);
		} catch (Exception e) {
			e.printStackTrace();
			return new UserSignInResponseDTO(1, "SERVER ERROR");
		}
	}
}
