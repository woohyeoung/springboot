package com.example.springboot.user.model;

import com.example.springboot.user.domain.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignInRequestDTO {

	private String email, password;

	public UserSignInRequestDTO(UserEntity userEntity) {
		this.email = userEntity.getEmail();
		this.password = userEntity.getPassword();
	}
}
