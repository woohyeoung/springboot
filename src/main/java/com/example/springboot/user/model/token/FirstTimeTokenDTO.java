package com.example.springboot.user.model.token;

import lombok.*;

@Getter
@Setter
@Builder
public class FirstTimeTokenDTO {
	private String accessToken;
	private ReIssuanceTokenDTO reIssuanceTokenDTO;
}
