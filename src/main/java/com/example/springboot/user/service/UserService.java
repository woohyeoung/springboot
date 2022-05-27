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
		UserDetails userDetails = userRepository.findByEmail(username);
		return userDetails;
	}

	public boolean validateUser(String email) {
		return userRepository.existsByEmail(email);
	}

	public String join(UserSignUpDTO sign) {
		try {
			if(!validateUser(sign.getEmail())) {
				System.out.println("ㅎㅎㅎ" + sign.getEmail()+ " " + sign.getPassword());
				UserEntity entity = userRepository.save(UserEntity.builder()
						.email(sign.getEmail())
						.password(passwordEncoder.encode(sign.getPassword()))
						.name(sign.getName())
						.build());
				return entity.getEmail();
			} else {
				return "ERROR : 이미 존재하는 정보입니다.";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String login(UserSignInDTO sign) {
		try {
			if(!validateUser(sign.getEmail()))
				return "ERROR : 가입되지 않은 E-Mail 입니다.";

			UserEntity user = userRepository.findByEmail(sign.getEmail());

			if(!passwordEncoder.matches(user.getPassword(), sign.getPassword()))
				return "ERROR : 잘못된 비밀번호 입니다.";

			return tokenProvider.createToken(user.getEmail(), user.getName());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
