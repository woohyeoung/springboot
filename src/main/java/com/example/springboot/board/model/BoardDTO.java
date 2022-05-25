package com.example.springboot.board.model;

import com.example.springboot.board.domain.Board;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@Getter
public class BoardDTO {

	private String title, content, author;
	private Date regDate;
	private int viewCount;

	@Builder
	public BoardDTO(String title, String content, String author, Date regDate, int viewCount) {
		this.title = title;
		this.content = content;
		this.author = author;
		this.regDate = regDate;
		this.viewCount = viewCount;
	}

	public Board toEntity() {
		return Board.builder()
				.title(title)
				.content(content)
				.author(author)
				.regDate(regDate)
				.viewCount(viewCount)
				.build();
	}
}
