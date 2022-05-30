package com.example.springboot.common.test;

import com.example.springboot.common.utils.Response;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@RequestMapping(value = "/api/test", method = RequestMethod.GET)
	public Response testGet() {
		return new Response(123, true, "Get Mapping Test");
	}
	@RequestMapping(value = "/api/test", method = RequestMethod.POST)
	public Response testPost(@RequestBody TestModel test) {
		return new Response(test.getId(), true, "Post Mapping Test" + test.getName());
	}
}
