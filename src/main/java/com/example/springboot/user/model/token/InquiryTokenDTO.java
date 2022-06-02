package com.example.springboot.user.model.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class InquiryTokenDTO {
	private String email, accessToken, refreshToken;
}
