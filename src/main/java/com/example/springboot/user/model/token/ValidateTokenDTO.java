package com.example.springboot.user.model.token;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ValidateTokenDTO {
	private int code;
	private String token;
}
