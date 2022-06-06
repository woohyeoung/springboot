package com.example.springboot.common.security.filter;

import com.example.springboot.common.config.properties.ConfigProperties;
import com.example.springboot.common.config.properties.TokenProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CustomCORSFilter {
	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern(ConfigProperties.CORS_PATTERN);
//		config.addAllowedOrigin(ConfigProperties.CLIENT_URL);
		config.addAllowedHeader(ConfigProperties.CORS_HEADER);
		config.addAllowedMethod(ConfigProperties.CORS_METHOD);
		config.addExposedHeader(TokenProperties.HEADER_KEY_ACCESS);
		config.addExposedHeader(TokenProperties.HEADER_KEY_REFRESH);
		source.registerCorsConfiguration(ConfigProperties.SOURCE_PATTERN, config);
		return new CorsFilter(source);
	}
}
