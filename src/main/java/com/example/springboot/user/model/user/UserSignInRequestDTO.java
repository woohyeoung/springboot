package com.example.springboot.user.model.user;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class UserSignInRequestDTO {
	private String email, password;
}
