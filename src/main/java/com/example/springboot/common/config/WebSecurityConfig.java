package com.example.springboot.common.config;

import com.example.springboot.common.security.AuthenticationFilter;
import com.example.springboot.common.security.TokenController;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.authentication.AuthenticationManagerFactoryBean;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {

	private final TokenController tokenController;
	private final CORSConfig config;

	@Bean
	AuthenticationManagerFactoryBean authenticationManagerFactoryBean() {
		return new AuthenticationManagerFactoryBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.addFilter(config.corsFilter()).csrf().disable()
				.addFilterBefore(new AuthenticationFilter(tokenController), UsernamePasswordAuthenticationFilter.class)
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().authorizeRequests()
					.antMatchers("/**").permitAll();
		return http.build();
	}
}
