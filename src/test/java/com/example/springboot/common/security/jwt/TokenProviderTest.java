package com.example.springboot.common.security.jwt;

import com.example.springboot.common.security.auth.CustomUserDetails;
import com.example.springboot.user.domain.user.UserEntity;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;


import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenProviderTest {

	@Autowired TokenProvider tokenProvider;

	@Test
	void 토큰_검증() {
		// given
		String em = "test123";
		String pw = "test123";
		String nm = "test123";

		UserEntity user = UserEntity.builder()
				.email(em).password(pw).name(nm).build();

		// when
		String accessToken = tokenProvider.generateAccessToken(user.getEmail());

		// then
		assertThat(tokenProvider.validateToken(accessToken)).isEqualTo(true);
		assertThat(tokenProvider.getUserPk(accessToken)).isEqualTo(user.getEmail());
	}
}