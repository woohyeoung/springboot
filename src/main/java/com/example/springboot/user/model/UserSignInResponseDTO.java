package com.example.springboot.user.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserSignInResponseDTO {
	private int key;
	private String message;
	private AccessResponseDTO accessResponseDTO;

	public UserSignInResponseDTO(int key, String message) {
		this.key = key;
		this.message = message;
	}

	public UserSignInResponseDTO(int key, AccessResponseDTO accessResponseDTO) {
		this.key = key;
		this.accessResponseDTO = accessResponseDTO;
	}
}
