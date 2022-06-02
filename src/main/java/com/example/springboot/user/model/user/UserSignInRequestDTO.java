package com.example.springboot.user.model.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSignInRequestDTO {
	private String email, password;
}
