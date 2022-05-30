package com.example.springboot.user.controller;

import com.example.springboot.common.response.ResponseDTO;
import com.example.springboot.user.domain.UserEntity;
import com.example.springboot.user.domain.UserRepository;
import com.example.springboot.user.model.UserSignUpRequestDTO;
import com.example.springboot.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

	@Autowired UserController userController;
	@Autowired UserRepository userRepository;
	@Autowired PasswordEncoder passwordEncoder;
	@Autowired UserService userService;

	@Autowired private TestRestTemplate restTemplate;

	@Test
	@Transactional
	void 회원가입() {
		// given
		UserSignUpRequestDTO signUpDTO = new UserSignUpRequestDTO(UserEntity.builder()
				.email("test123@example.com")
				.password("test123")
				.name("test")
				.build());
		String url = "http://localhost:8080/api/user/sign";

		// when
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, signUpDTO, String.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isGreaterThan("0");

		List<UserEntity> all = userRepository.findAll();
		assertThat(all.get(0).getEmail()).isEqualTo("test123@example.com");
		assertThat(all.get(0).getName()).isEqualTo("test");
	}

	@Test
	void 중복_회원() {
		// given
		UserSignUpRequestDTO test1 = new UserSignUpRequestDTO(UserEntity.builder()
				.email("test@example.com")
				.password("test123")
				.name("test")
				.build());
		UserSignUpRequestDTO test2 = new UserSignUpRequestDTO(UserEntity.builder()
				.email("test@example.com")
				.password("test123")
				.name("test")
				.build());

		String url = "http://localhost:8080/api/user/sign";

		// when
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, test1, String.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isGreaterThan("0");

		ResponseEntity<ResponseDTO> responseEntity1 = restTemplate.postForEntity(url, test2, ResponseDTO.class);
		assertThat(responseEntity1.getBody().getMessage()).isEqualTo("회원가입에 실패하였습니다.ERROR : 이미 존재하는 정보입니다.");
	}
}