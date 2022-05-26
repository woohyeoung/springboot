package com.example.springboot.user.model;

import com.example.springboot.user.domain.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignUpDTO {

	private String email, password, name;

	public UserSignUpDTO(UserEntity user) {
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.name = user.getName();
	}
}
