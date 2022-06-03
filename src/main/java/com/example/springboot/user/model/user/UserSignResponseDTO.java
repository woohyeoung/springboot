package com.example.springboot.user.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSignResponseDTO {
	private String email, message;
}
