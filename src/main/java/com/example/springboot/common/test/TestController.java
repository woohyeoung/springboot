package com.example.springboot.common.test;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@RequestMapping(value = "/api/test", method = RequestMethod.GET)
	public TestModel test() {
		TestModel testModel = new TestModel();
		testModel.setName("testName");
		testModel.setId(123);
		return testModel;
	}
}
