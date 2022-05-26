package com.example.springboot.user.controller;

import com.example.springboot.common.security.TokenProvider;
import com.example.springboot.user.domain.UserEntity;
import com.example.springboot.user.domain.UserRepository;
import com.example.springboot.user.model.UserSignInDTO;
import com.example.springboot.user.model.UserSignUpDTO;
import com.example.springboot.user.model.UserValidDTO;
import com.example.springboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

	private final UserRepository userRepository;
	private final UserService userService;

	@PostMapping("/join")
	public UserValidDTO join(@RequestBody UserSignUpDTO userSignUpDTO) {
		try {
			UserValidDTO user = new UserValidDTO(userService.join(userSignUpDTO).getEmail());
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@PostMapping("/login")
	public String login(@RequestBody UserSignInDTO userSignInDTO) {
		try {
			return userService.login(userSignInDTO);
		} catch (Exception e) {
			System.out.println("로그인 오류");
			e.printStackTrace();
			return null;
		}
	}
}
