package com.example.springboot.user.controller;

import com.example.springboot.common.response.Payload;
import com.example.springboot.common.response.ResponseDTO;
import com.example.springboot.user.model.user.UserSignInRequestDTO;
import com.example.springboot.user.model.user.UserSignInResponseDTO;
import com.example.springboot.user.model.user.UserSignUpRequestDTO;
import com.example.springboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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
		String result = userService.join(userSignUpRequestDTO);
		if(result.startsWith("ERROR")) return new ResponseDTO().fail(HttpStatus.BAD_REQUEST, Payload.SIGN_UP_FAIL + result);
		if(result.startsWith("SERVER")) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR, Payload.SERVER_ERROR + "UserController join()");

		return new ResponseDTO().of(HttpStatus.OK, Payload.SIGN_UP_OK);
	}

	@PostMapping("/login")
	public ResponseDTO login(@RequestBody UserSignInRequestDTO userSignInRequestDTO) {
		logger.info("UserController login method ...");
		UserSignInResponseDTO result = userService.login(userSignInRequestDTO);

		if(result.getKey() == 0) return new ResponseDTO(HttpStatus.BAD_REQUEST, Payload.SIGN_IN_FAIL + result.getMessage());
		if(result.getKey() == 1) return new ResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR, Payload.SERVER_ERROR + "UserController login()");

		return new ResponseDTO(HttpStatus.OK, Payload.SIGN_IN_OK);
	}

	@PostMapping(path = "/logout", headers = "Authorization")
	public ResponseDTO logout(@RequestHeader HttpHeaders headers) {
		logger.info("UserController logout method ...");
		String result = userService.logout(String.valueOf(headers.get("Authorization")));

		if(result.startsWith("ERROR")) return new ResponseDTO().fail(HttpStatus.BAD_REQUEST, Payload.SIGN_OUT_FAIL);
		if(result.startsWith("SERVER")) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR, Payload.SERVER_ERROR + "UserController logout()");

		return new ResponseDTO().of(HttpStatus.OK, Payload.SIGN_OUT_OK, result);
	}
}
