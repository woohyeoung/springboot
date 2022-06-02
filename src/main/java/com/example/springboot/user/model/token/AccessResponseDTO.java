package com.example.springboot.user.model.token;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessResponseDTO {
	private String accessToken;
	private InquiryTokenDTO inquiryTokenDTO;
}
