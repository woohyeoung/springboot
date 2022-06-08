package com.example.springboot.common.config;

import com.example.springboot.common.security.handler.BcryptHandler;
import com.example.springboot.common.security.handler.RoleInterceptorHandler;
import com.example.springboot.common.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
	private final BcryptHandler bcryptHandler;
	private final TokenProvider tokenProvider;
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new RoleInterceptorHandler(bcryptHandler, tokenProvider))
				.order(1)
				.addPathPatterns("/**")
				.excludePathPatterns("/sign");
	}
}
