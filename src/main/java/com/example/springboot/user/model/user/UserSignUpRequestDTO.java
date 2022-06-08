package com.example.springboot.user.model.user;

import com.example.springboot.common.config.properties.RoleProperties;
import com.example.springboot.user.domain.user.UserEntity;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserSignUpRequestDTO {
	private String email, password, name;

	public UserEntity toEntity(String pw) {
		return UserEntity.builder()
						.email(email)
						.password(pw)
						.name(name)
						.role(RoleProperties.USER_ROLE)
						.accessDate(new Date())
						.build();
	}
}
