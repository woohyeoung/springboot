package com.example.springboot.common.config;

import com.example.springboot.common.security.auth.CustomUserDetailService;
import com.example.springboot.common.security.filter.AuthenticationFilter;
import com.example.springboot.common.security.filter.AuthorizationFilter;
import com.example.springboot.common.security.jwt.TokenProvider;
import com.example.springboot.user.domain.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.authentication.AuthenticationManagerFactoryBean;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
	private final CORSConfig config;
	private final TokenRepository tokenRepository;
	private final TokenProvider tokenProvider;
	private final CustomUserDetailService customUserDetailService;

	@Bean
	AuthenticationManagerFactoryBean authenticationManagerFactoryBean() {
		return new AuthenticationManagerFactoryBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.addFilter(config.corsFilter()).csrf().disable()
				.addFilter(new AuthenticationFilter(authenticationManagerFactoryBean().getObject(), tokenProvider, tokenRepository))
				.addFilter(new AuthorizationFilter(authenticationManagerFactoryBean().getObject(), tokenProvider, customUserDetailService))
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().authorizeRequests()
					.antMatchers("/**").permitAll();
		return http.build();
	}
}
