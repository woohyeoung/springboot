package com.example.springboot.common.test;

import com.example.springboot.common.response.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@RequestMapping(value = "/api/test", method = RequestMethod.GET)
	public ResponseDTO testGet() {
		return new ResponseDTO().of(HttpStatus.OK, "Get Mapping Test");
	}
	@RequestMapping(value = "/api/test", method = RequestMethod.POST)
	public ResponseDTO testPost(@RequestBody TestModel test) {
		return new ResponseDTO().of(HttpStatus.OK, "Post Mapping Test" + test.getName());
	}
}
