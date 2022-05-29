package com.example.springboot.common.test;

import com.example.springboot.common.utils.Payload;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@RequestMapping(value = "/api/test", method = RequestMethod.GET)
	public Payload testGet() {
		return new Payload(123, "Get Mapping Test");
	}
	@RequestMapping(value = "/api/test", method = RequestMethod.POST)
	public Payload testPost(@RequestBody TestModel test) {
		return new Payload(test.getId(), "Post Mapping Test" + test.getName());
	}
}
