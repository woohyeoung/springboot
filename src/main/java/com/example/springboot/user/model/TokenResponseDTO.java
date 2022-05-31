package com.example.springboot.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TokenResponseDTO {
	private String email, refreshToken;
	private Long RTExp;
}
