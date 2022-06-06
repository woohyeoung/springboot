package com.example.springboot.common.config;

import com.example.springboot.common.config.properties.ConfigProperties;
import com.example.springboot.common.config.properties.RoleProperties;
import com.example.springboot.common.security.auth.CustomUserDetailService;
import com.example.springboot.common.security.filter.*;
import com.example.springboot.common.security.handler.CustomLogoutHandler;
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
	private final CustomCORSFilter corsFilter;
	private final TokenRepository tokenRepository;
	private final TokenProvider tokenProvider;
	private final CustomUserDetailService customUserDetailService;
	private final CustomAuthenticationManager customAuthenticationManager;
	private final CustomLogoutHandler customLogoutHandler;
	private final CustomLogoutSuccessHandler customLogoutSuccessHandler;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.httpBasic().disable().csrf().disable().formLogin().disable()
				.logout()
					.logoutUrl("/my/logout")
					.deleteCookies(ConfigProperties.SESSION_ID)
					.addLogoutHandler(customLogoutHandler)
					.logoutSuccessHandler(customLogoutSuccessHandler)
				.and()
					.addFilter(corsFilter.corsFilter())
					.addFilter(new CustomAuthenticationFilter(customAuthenticationManager, tokenProvider, tokenRepository))
					.addFilter(new CustomAuthorizationFilter(customAuthenticationManager, tokenProvider, customUserDetailService))
					.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
					.authorizeRequests()
						.antMatchers(ConfigProperties.USER_ROLE).hasRole(RoleProperties.USER_ROLE)
						.antMatchers(ConfigProperties.PERMIT_ALL).permitAll();
		return http.build();
	}
}
