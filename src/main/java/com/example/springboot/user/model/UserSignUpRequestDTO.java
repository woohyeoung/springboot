package com.example.springboot.user.model;

import com.example.springboot.user.domain.UserEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpRequestDTO {

	private String email, password, name;

	public UserSignUpRequestDTO(UserEntity user) {
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.name = user.getName();
	}
}
