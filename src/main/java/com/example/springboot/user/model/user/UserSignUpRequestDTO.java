package com.example.springboot.user.model.user;

import com.example.springboot.user.domain.user.UserEntity;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserSignUpRequestDTO {
	private String email, password, name;
//	private final String roles;

	public UserEntity toEntity(BCryptPasswordEncoder passwordEncoder) {
		return UserEntity.builder()
						.email(email)
						.password(passwordEncoder.encode(password))
						.name(name)
//						.roles(roles)
						.build();
	}
}
