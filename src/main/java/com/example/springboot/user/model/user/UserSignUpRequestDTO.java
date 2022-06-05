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
	private String email, password, name;
//	private final String roles;

	public UserEntity toEntity(String pw) {
		return UserEntity.builder()
						.email(email)
						.password(pw)
						.name(name)
//						.roles(roles)
						.build();
	}
}
