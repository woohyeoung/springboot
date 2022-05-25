package com.example.springboot.board.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_board")
public class Board {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer boardNo;

	@Column(length = 30, nullable = false) 				 private String title;
	@Column(columnDefinition = "TEXT", nullable = false) private String content;
	@Column(length = 20, nullable = false) 				 private String author;
	@Column(nullable = false) 							 private Date regDate;
	@Column(length = 10, nullable = false) 				 private int viewCount;
}
