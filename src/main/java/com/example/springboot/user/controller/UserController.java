package com.example.springboot.user.controller;

import com.example.springboot.common.utils.Response;
import com.example.springboot.user.model.UserSignInRequestDTO;
import com.example.springboot.user.model.UserSignUpRequestDTO;
import com.example.springboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	public Response join(@RequestBody UserSignUpRequestDTO userSignUpRequestDTO) {
		logger.info("UserService join method ...");
		String result = userService.join(userSignUpRequestDTO);

		if(result.startsWith("ERROR")) return new Response(0, false, result);
		if(result.startsWith("SERVER")) return new Response(1, false, result);

		return new Response(2, true, result);
	}

	@PostMapping("/login")
	public Response login(@RequestBody UserSignInRequestDTO userSignInRequestDTO) {
		logger.info("UserService login method ...");
		String result = userService.login(userSignInRequestDTO);

		if(result.startsWith("ERROR")) return new Response(0, false, result);
		if(result.startsWith("SERVER")) return new Response(1, false, result);

		return new Response(2, true,  result);
	}


}
