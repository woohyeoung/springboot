package com.example.springboot.common.config;

import com.example.springboot.common.security.auth.CustomUserDetailService;
import com.example.springboot.common.security.filter.CustomAuthenticationFilter;
import com.example.springboot.common.security.filter.CustomAuthenticationManager;
import com.example.springboot.common.security.filter.CustomAuthorizationFilter;
import com.example.springboot.common.security.handler.CustomLogoutSuccessHandler;
import com.example.springboot.common.security.jwt.TokenProvider;
import com.example.springboot.user.domain.token.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig {
	private final CORSConfig config;
	private final TokenRepository tokenRepository;
	private final TokenProvider tokenProvider;
	private final CustomUserDetailService customUserDetailService;
	private final CustomAuthenticationManager customAuthenticationManager;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.httpBasic().disable().csrf().disable().formLogin().disable()
					.logout().logoutSuccessHandler(new CustomLogoutSuccessHandler())
				.and()
					.addFilter(config.corsFilter())
					.addFilter(new CustomAuthenticationFilter(customAuthenticationManager, tokenProvider, tokenRepository))
					.addFilter(new CustomAuthorizationFilter(customAuthenticationManager, tokenProvider, customUserDetailService))
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
					.authorizeRequests()
						.antMatchers(ConfigProperties.USER_ROLE).access("hasRole('USER')")
						.antMatchers(ConfigProperties.PERMIT_ALL).permitAll();
		return http.build();
	}
}
