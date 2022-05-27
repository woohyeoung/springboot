package com.example.springboot.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CORSConfig {
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true); // 내 서버가 응답할 때 json을 자바스크립트에서 처리할 수 있게 할지 설정하는 것
		config.addAllowedOriginPattern("*"); // 모든 ip에 응답을 허용함
		config.addAllowedHeader("*"); // 모든 header에 응답을 허용함
		config.addAllowedMethod("*"); // 모든 post, get, delete, put 요청 허용함
		source.registerCorsConfiguration("/**", config); // /api로 들어오는 모든 주소에 config 설정
		return new CorsFilter(source);
	}
}
