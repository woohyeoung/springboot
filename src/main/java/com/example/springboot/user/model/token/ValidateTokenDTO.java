package com.example.springboot.user.model.token;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class ValidateTokenDTO {
	private int code;
	private String token;
}
