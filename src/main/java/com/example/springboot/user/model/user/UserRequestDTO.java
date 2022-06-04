package com.example.springboot.user.model.user;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {
	private String email;
}
