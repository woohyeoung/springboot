package com.example.springboot.common.config;

import com.example.springboot.common.security.jwt.TokenProperties;
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
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*");
//		config.addAllowedOrigin(ConfigProperties.CLIENT_URL);
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.addExposedHeader(TokenProperties.HEADER_KEY_ACCESS);
		config.addExposedHeader(TokenProperties.HEADER_KEY_REFRESH);
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}
