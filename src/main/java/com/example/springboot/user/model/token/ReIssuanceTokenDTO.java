package com.example.springboot.user.model.token;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ReIssuanceTokenDTO {
	private String email, refreshToken;
}
