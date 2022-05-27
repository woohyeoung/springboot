package com.example.springboot.user.controller;

import com.example.springboot.common.security.TokenProvider;
import com.example.springboot.common.utils.Payload;
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

	private final UserService userService;

	@PostMapping("/sign")
	public Payload join(@RequestBody UserSignUpDTO userSignUpDTO) {
		String response = userService.join(userSignUpDTO);

		if(response == null || response.startsWith("ERROR")) return new Payload(0, "이미 존재하는 정보입니다.");

		return new Payload(1, response);
	}

	@PostMapping("/login")
	public Payload login(@RequestBody UserSignInDTO userSignInDTO) {
		String response = userService.login(userSignInDTO);

		if(response == null || response.startsWith("ERROR")) return new Payload(0, "E-Mail 또는 비밀번호가 일치하지 않습니다.");

		return new Payload(1, response);
	}
}
