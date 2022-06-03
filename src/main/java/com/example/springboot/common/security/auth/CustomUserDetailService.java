package com.example.springboot.common.security.auth;

import com.example.springboot.user.domain.user.UserEntity;
import com.example.springboot.user.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailService implements UserDetailsService {
	private final Logger logger = LoggerFactory.getLogger(CustomUserDetailService.class);
	private final UserRepository userRepository;


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("CustomUserDetailService - loadUserByUsername() ...");
		UserEntity user = userRepository.findByEmail(username);

		if(user == null) {
			logger.error("유저가 존재하지 않습니다. loadUserByUsername()");
		}
		return new CustomUserDetails(user);
	}
}
