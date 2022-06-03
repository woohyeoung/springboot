package com.example.springboot.user.domain.token;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "tbl_token")
@Entity
public class TokenEntity {
	@Id @Column(length = 150, nullable = false) private String email;
	@Column(length = 500, nullable = false) 	private String accessToken;
	@Column(length = 500, nullable = false) 	private String refreshToken;
}
