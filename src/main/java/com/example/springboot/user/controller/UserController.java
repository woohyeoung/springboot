package com.example.springboot.user.controller;

import com.example.springboot.common.response.Payload;
import com.example.springboot.common.response.ResponseDTO;
import com.example.springboot.user.model.user.UserSignResponseDTO;
import com.example.springboot.user.model.user.UserSignUpRequestDTO;
import com.example.springboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {
	private final UserService userService;

	@PostMapping("/sign")
	public ResponseDTO join(@RequestBody UserSignUpRequestDTO userSignUpRequestDTO) {
		log.info("UserController join method ...");

		UserSignResponseDTO result = userService.join(userSignUpRequestDTO);

		if(result.getKey() == 0) return new ResponseDTO().fail(HttpStatus.BAD_REQUEST, Payload.SIGN_UP_FAIL + result.getMessage());
		if(result.getKey() == 1) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR, Payload.SERVER_ERROR + "UserController join()");

		return new ResponseDTO().of(HttpStatus.OK, Payload.SIGN_UP_OK);
	}

	@PostMapping(value = "/api_user/token_check")
	public ResponseDTO tokenCheck() {
		log.info("tokenCheck() ...");

		return new ResponseDTO().of(HttpStatus.OK, "");
	}
	@GetMapping(value = "/api_admin/user/role_check")
	public ResponseDTO roleCheck() {
		log.info("roleCheck() ...");

		return new ResponseDTO().of(HttpStatus.OK, "");
	}
}
