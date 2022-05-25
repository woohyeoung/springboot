package com.example.springboot.common;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/")
	public Test test(@RequestBody String name) {
		Test test = new Test(name);

		return test;
	}

}
@AllArgsConstructor
class Test {
	public String name;
}
