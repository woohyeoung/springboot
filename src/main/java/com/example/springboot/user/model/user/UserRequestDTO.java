package com.example.springboot.user.model.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class UserRequestDTO {
	private String email;
}
