package com.example.springboot.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AccessResponseDTO {
	private String accessToken;
	private TokenResponseDTO tokenResponseDTO;
}
