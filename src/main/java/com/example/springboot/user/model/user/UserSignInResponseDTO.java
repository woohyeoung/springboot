package com.example.springboot.user.model.user;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
public class UserSignInResponseDTO {
	private int key;
	private String message;
}
