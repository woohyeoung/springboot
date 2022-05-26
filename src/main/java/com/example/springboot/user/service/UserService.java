package com.example.springboot.user.service;

import com.example.springboot.common.security.TokenProvider;
import com.example.springboot.user.domain.UserEntity;
import com.example.springboot.user.domain.UserRepository;
import com.example.springboot.user.model.UserSignInDTO;
import com.example.springboot.user.model.UserSignUpDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final TokenProvider tokenProvider;

	@Autowired
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다. userName = " + username));
	}

	public void validateUser(UserSignUpDTO userSignUpDTO) {
		userRepository.findByEmail(userSignUpDTO.getEmail())
				.ifPresent(m -> new IllegalArgumentException("이미 존재하는 회원입니다."));
	}

	public UserSignUpDTO join(UserSignUpDTO userSignUpDTO) {
		try {
			validateUser(userSignUpDTO);

			UserEntity userEntity = userRepository.save(UserEntity.builder()
					.email(userSignUpDTO.getEmail())
					.password(passwordEncoder.encode(userSignUpDTO.getPassword()))
					.name(userSignUpDTO.getName())
					.roles(Collections.singletonList("ROLE_USER"))
					.build());
			return new UserSignUpDTO(userEntity);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String login(UserSignInDTO userSignInDTO) {
		UserEntity userEntity = userRepository.findByEmail(userSignInDTO.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("가입되지 않은 E-Mail 입니다. E-Mail = " + userSignInDTO.getEmail()));
		if (!passwordEncoder.matches(userSignInDTO.getPassword(), userEntity.getPassword())) {
			throw new IllegalArgumentException("잘못된 비밀번호입니다.");
		}
		return tokenProvider.createToken(userEntity.getUsername(), userEntity.getRoles());
	}
}
