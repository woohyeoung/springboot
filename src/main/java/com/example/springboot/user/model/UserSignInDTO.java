package com.example.springboot.user.model;

import com.example.springboot.user.domain.UserEntity;
import lombok.Getter;

@Getter
public class UserSignInDTO {

	private String email, password;

	public UserSignInDTO(UserEntity userEntity) {
		this.email = userEntity.getEmail();
		this.password = userEntity.getPassword();
	}
}
