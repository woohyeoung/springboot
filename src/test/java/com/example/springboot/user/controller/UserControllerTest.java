package com.example.springboot.user.controller;

import com.example.springboot.common.response.ResponseDTO;
import com.example.springboot.common.security.jwt.TokenProvider;
import com.example.springboot.user.domain.token.TokenEntity;
import com.example.springboot.user.domain.token.TokenRepository;
import com.example.springboot.user.domain.user.UserEntity;
import com.example.springboot.user.domain.user.UserRepository;
import com.example.springboot.user.model.token.FirstTimeTokenDTO;
import com.example.springboot.user.model.user.UserRequestDTO;
import com.example.springboot.user.model.user.UserSignInRequestDTO;
import com.example.springboot.user.model.user.UserSignResponseDTO;
import com.example.springboot.user.model.user.UserSignUpRequestDTO;
import com.example.springboot.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
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
	@Autowired TokenProvider tokenProvider;
	@Autowired TokenRepository tokenRepository;

	@Autowired private TestRestTemplate restTemplate;

	@Test
	@Transactional
	void 회원가입() {
		// given
		UserSignUpRequestDTO signUpDTO = UserSignUpRequestDTO.builder()
				.email("test123@example.com")
				.password("test123")
				.name("test")
//				.roles("USER")
				.build();
		String url = "http://localhost:8080/sign";

		// when
		ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url, signUpDTO, Object.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

		List<UserEntity> all = userRepository.findAll();
		assertThat(all.get(0).getEmail()).isEqualTo("test123@example.com");
		assertThat(all.get(0).getName()).isEqualTo("test");
	}

	@Test
	void 중복_회원() {
		// given
		UserSignUpRequestDTO test1 = UserSignUpRequestDTO.builder()
				.email("test@example.com")
				.password("test123")
				.name("test")
				.build();
		UserSignUpRequestDTO test2 = UserSignUpRequestDTO.builder()
				.email("test@example.com")
				.password("test123")
				.name("test")
				.build();

		String url = "http://localhost:8080/sign";

		// when
		ResponseEntity<ResponseDTO> responseEntity = restTemplate.postForEntity(url, test1, ResponseDTO.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody().getStatus()).isEqualTo(HttpStatus.OK);

		ResponseEntity<ResponseDTO> responseEntity1 = restTemplate.postForEntity(url, test2, ResponseDTO.class);
		assertThat(responseEntity1.getBody().getMessage()).isEqualTo("회원가입에 실패하였습니다.이미 존재하는 정보입니다.");
	}

	@Test
	void 로그인() {
		// given
		String em = "test1234@example.com";
		String pw = "test123";
		String url = "http://localhost:8080";

		UserSignUpRequestDTO signUp = UserSignUpRequestDTO.builder()
				.email(em)
				.password(pw)
				.name("test")
				.build();

		UserSignInRequestDTO signIn = UserSignInRequestDTO.builder().email(em).password(pw).build();

		// when
		ResponseEntity<Object> responseEntity1 = restTemplate.postForEntity(url + "/sign", signUp, Object.class);

		// then
		ResponseEntity<String> responseEntity2 = restTemplate.postForEntity(url + "/login", signIn, String.class);
		System.out.println(responseEntity1.getBody());
		System.out.println(responseEntity2.getBody());
		System.out.println(responseEntity2.getHeaders().get("Authorization"));
		System.out.println(responseEntity2.getHeaders().get("RefreshToken"));
		assertThat(responseEntity2.getHeaders().get("Authorization")).isNotEqualTo(null);
		assertThat(responseEntity2.getHeaders().get("RefreshToken")).isNotEqualTo(null);
	}

	@Test
	void 로그인_실패() {
		// given
		String em = "test1234@example.com";
		String pw = "test123";
		String url = "http://localhost:8080";

		UserSignUpRequestDTO signUp = UserSignUpRequestDTO.builder()
				.email(em)
				.password(pw)
				.name("test")
				.build();

		UserSignInRequestDTO signIn1 = UserSignInRequestDTO.builder().email("test@examle.com").password(pw).build();
		UserSignInRequestDTO signIn2 = UserSignInRequestDTO.builder().email(em).password("test").build();
		// when
		ResponseEntity<Object> responseEntity = restTemplate.postForEntity(url + "/sign", signUp, Object.class);

		// then
		ResponseEntity<Object> responseEntity1 = restTemplate.postForEntity(url + "/login", signIn1, Object.class);
		ResponseEntity<Object> responseEntity2 = restTemplate.postForEntity(url + "/login", signIn2, Object.class);

		System.out.println(responseEntity.getBody());
		System.out.println(responseEntity1.getBody());
		System.out.println(responseEntity1.getHeaders().get("Authorization"));
		System.out.println(responseEntity2.getBody());
		System.out.println(responseEntity2.getHeaders().get("Authorization"));
		assertThat(responseEntity1.getHeaders().get("Authorization")).isEqualTo(null);
		assertThat(responseEntity2.getHeaders().get("Authorization")).isEqualTo(null);
	}

	@Test
	void 토큰_재발급() {
		// given
		String em = "test123";
		String pw = "test123";
		String nm = "test123";

		UserEntity user = UserEntity.builder()
				.email(em).password(pw).name(nm).build();

		String url = "http://localhost:8080";

		// when
		restTemplate.postForEntity(url + "/sign", user, String.class);
		ResponseEntity<String> responseEntity2 = restTemplate.postForEntity(url + "/login", user, String.class);

		String refresh = String.valueOf(responseEntity2.getHeaders().get("RefreshToken"));

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("RefreshToken", refresh);

		HttpEntity<String> request = new HttpEntity<>("", httpHeaders);

		Object response = restTemplate.postForObject(url + "/token_check", request, String.class);

		// then
		System.out.println(refresh);
		System.out.println(response);
//		assertThat()

	}
	@Test
	void 로그아웃() {
		// given
		String em = "test12@example.com";
		String pw = "test123";
		String url = "http://localhost:8080";

		UserSignUpRequestDTO signUp = UserSignUpRequestDTO.builder()
				.email(em)
				.password(pw)
				.name("test")
				.build();

		UserSignInRequestDTO signIn = UserSignInRequestDTO.builder().email(em).password(pw).build();

		restTemplate.postForEntity(url + "/sign", signUp, String.class);
		ResponseEntity<String> responseEntity2 = restTemplate.postForEntity(url + "/login", signIn, String.class);

		// when
		String token =  String.valueOf(responseEntity2.getHeaders().get("Authorization"));
		String refresh = String.valueOf(responseEntity2.getHeaders().get("RefreshToken"));
		System.out.println(token);
		System.out.println(refresh);
		String answer = "";

		UserRequestDTO userRequestDTO = UserRequestDTO.builder().email(em).build();

		if(tokenProvider.validateToken(token.replace("Bearer ", ""))) answer = "ok";
		if(tokenProvider.validateToken(refresh.replace("Refresh ", ""))) answer += "2";
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url + "/logout", userRequestDTO, String.class);

		System.out.println(responseEntity.getBody());
		// then
		assertThat(tokenProvider.validateToken(token.replace("Bearer ", ""))).isEqualTo(false);
		assertThat(answer).isEqualTo("ok2");
	}
}