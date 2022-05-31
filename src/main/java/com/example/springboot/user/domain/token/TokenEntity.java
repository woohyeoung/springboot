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
	@Id @Column(length = 100, nullable = false) private String email;
	@Column(length = 300, nullable = false) 	private String refreshToken;
	@Column(length = 30, nullable = false)  	private long refreshTime;
}
