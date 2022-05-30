package com.example.springboot.user.model;

import com.example.springboot.user.domain.UserEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSignInRequestDTO {

	private String email, password;

	public UserSignInRequestDTO(UserEntity user) {
		this.email = user.getEmail();
		this.password = user.getPassword();
	}
}
