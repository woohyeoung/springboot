package com.example.springboot.board.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tbl_board")
@Entity
public class BoardEntity extends BaseTimeEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer boardNo;

	@Column(length = 30, nullable = false) 				 private String title;
	@Column(columnDefinition = "TEXT", nullable = false) private String content;
	@Column(length = 20, nullable = false) 				 private String author;

	public void update(String title, String content) {
		this.title = title;
		this.content = content;
	}
}
