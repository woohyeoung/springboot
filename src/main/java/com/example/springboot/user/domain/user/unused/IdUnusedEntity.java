package com.example.springboot.user.domain.user.unused;

import com.example.springboot.board.domain.BaseTimeEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tbl_unused_id")
public class IdUnusedEntity extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 100, nullable = false, unique = true)  private String email;
	@Column(length = 20, nullable = false)					private String name;
	@Column(nullable = false)								private Date accessDate;
}
