package com.example.springboot.user.model;

import com.example.springboot.user.domain.UserEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSignUpRequestDTO {

	private String email, password, name;

	public UserSignUpRequestDTO(UserEntity user) {
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.name = user.getName();
	}
}
