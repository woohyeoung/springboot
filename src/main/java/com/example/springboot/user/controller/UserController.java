package com.example.springboot.user.controller;

import com.example.springboot.common.response.Payload;
import com.example.springboot.common.response.ResponseDTO;
import com.example.springboot.common.response.ResponseHeaderDTO;
import com.example.springboot.user.model.UserSignInRequestDTO;
import com.example.springboot.user.model.UserSignUpRequestDTO;
import com.example.springboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);
	private final UserService userService;

	@PostMapping("/sign")
	public ResponseDTO join(@RequestBody UserSignUpRequestDTO userSignUpRequestDTO) {
		logger.info("UserController join method ...");
		String result = userService.join(userSignUpRequestDTO);
		if(result.startsWith("ERROR")) return new ResponseDTO().fail(HttpStatus.BAD_REQUEST, Payload.SIGN_UP_FAIL + result);
		if(result.startsWith("SERVER")) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR, Payload.SERVER_ERROR + "UserController join()");

		return new ResponseDTO().of(HttpStatus.OK, Payload.SIGN_UP_OK);
	}

	@PostMapping("/login")
	public ResponseEntity login(@RequestBody UserSignInRequestDTO userSignInRequestDTO) {
		logger.info("UserController login method ...");
		String result = userService.login(userSignInRequestDTO);

		if(result.startsWith("ERROR")) return new ResponseEntity(new ResponseHeaderDTO(Payload.SIGN_IN_FAIL + result), HttpStatus.BAD_REQUEST);
		if(result.startsWith("SERVER")) return new ResponseEntity(new ResponseHeaderDTO(Payload.SERVER_ERROR + "UserController login()"), HttpStatus.INTERNAL_SERVER_ERROR);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Authorization", result);

		return new ResponseEntity(new ResponseHeaderDTO(Payload.SIGN_IN_OK), httpHeaders, HttpStatus.OK);
	}

}
