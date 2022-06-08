package com.example.springboot.user.model.user;

import com.example.springboot.user.domain.user.UserEntity;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserSignUpRequestDTO {
	private String email, password, name, role;

	public UserEntity toEntity(String pw) {
		return UserEntity.builder()
						.email(email)
						.password(pw)
						.name(name)
						.role(role)
						.build();
	}
}
