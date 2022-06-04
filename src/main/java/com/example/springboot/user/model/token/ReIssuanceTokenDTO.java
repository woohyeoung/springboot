package com.example.springboot.user.model.token;

import lombok.*;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
public class ReIssuanceTokenDTO {
	private String email, refreshToken;
}
