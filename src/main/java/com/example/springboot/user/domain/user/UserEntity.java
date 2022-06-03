package com.example.springboot.user.domain.user;

import com.example.springboot.board.domain.BaseTimeEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
//	@Column(length = 50, nullable = false)					private String roles;
//
//	public List<String> getRoleList() {
//		if(this.roles.length() > 0) return Arrays.asList(this.roles.split(","));
//
//		return new ArrayList<>();
//	}
}
