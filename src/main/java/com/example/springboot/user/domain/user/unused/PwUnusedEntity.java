package com.example.springboot.user.domain.user.unused;

import com.example.springboot.board.domain.BaseTimeEntity;

import javax.persistence.*;

@Entity
@Table(name = "tbl_unused_pw")
public class PwUnusedEntity extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 300, nullable = false)					private String password;
	@Column(length = 20, nullable = false)					private String role;
}
