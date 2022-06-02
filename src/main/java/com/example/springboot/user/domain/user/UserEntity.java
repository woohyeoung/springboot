package com.example.springboot.user.domain.user;

import com.example.springboot.board.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_user")
@Entity
public class UserEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100, nullable = false, unique = true)  private String email;
	@Column(length = 300, nullable = false)					private String password;
	@Column(length = 20, nullable = false)					private String name;
}
