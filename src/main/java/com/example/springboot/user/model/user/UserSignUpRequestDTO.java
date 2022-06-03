package com.example.springboot.user.model.user;

import com.example.springboot.user.domain.user.UserEntity;
import lombok.*;

@Getter
@Setter
@Builder
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
