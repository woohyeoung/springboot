package com.example.springboot.user.controller;

import com.example.springboot.common.response.Payload;
import com.example.springboot.common.response.ResponseDTO;
import com.example.springboot.common.security.jwt.TokenProperties;
import com.example.springboot.user.model.user.UserRequestDTO;
import com.example.springboot.user.model.user.UserSignInRequestDTO;
import com.example.springboot.user.model.user.UserSignResponseDTO;
import com.example.springboot.user.model.user.UserSignUpRequestDTO;
import com.example.springboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final Logger logger = LoggerFactory.getLogger(UserController.class);
	private final UserService userService;

	@PostMapping("/sign")
	public ResponseDTO join(@RequestBody UserSignUpRequestDTO userSignUpRequestDTO) {
		logger.info("UserController join method ...");

		UserSignResponseDTO result = userService.join(userSignUpRequestDTO);

		if(result.getKey() == 0) return new ResponseDTO().fail(HttpStatus.BAD_REQUEST, Payload.SIGN_UP_FAIL + result.getMessage());
		if(result.getKey() == 1) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR, Payload.SERVER_ERROR + "UserController join()");

		return new ResponseDTO().of(HttpStatus.OK, Payload.SIGN_UP_OK);
	}

//	@PostMapping("/login")
//	public ResponseDTO login(@RequestBody UserSignInRequestDTO userSignInRequestDTO) {
//		logger.info("UserController login method ...");
//
//		UserSignResponseDTO result = userService.login(userSignInRequestDTO);
//
//		if(result.getKey() == 1) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR, Payload.SERVER_ERROR + "UserController login()");
//
//		return new ResponseDTO().of(HttpStatus.OK, Payload.SIGN_IN_OK, result.getMessage());
//	}

	@GetMapping("/logout")
	public ResponseDTO logout(@RequestBody UserRequestDTO userRequestDTO) {
		logger.info("UserController logout method ...");

		UserSignResponseDTO result = userService.logout(userRequestDTO);

		if(result.getKey() == 0) return new ResponseDTO().fail(HttpStatus.BAD_REQUEST, Payload.SIGN_OUT_FAIL + result.getMessage());
		if(result.getKey() == 1) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR, Payload.SERVER_ERROR + "UserController logout()");

		return new ResponseDTO().of(HttpStatus.OK, Payload.SIGN_OUT_OK, result.getMessage());
	}

	@PostMapping(value = "/token_check")
	public ResponseDTO tokenCheck() {
		logger.info("tokenCheck() ...");

		return new ResponseDTO().of(HttpStatus.OK, "");
	}
}
