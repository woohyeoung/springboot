package com.example.springboot.board.model;

import com.example.springboot.board.domain.BoardEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Getter
public class BoardRequestDTO {

	private String title, content, author;

	@Builder
	public BoardRequestDTO(String title, String content, String author) {
		this.title = title;
		this.content = content;
		this.author = author;
	}

	public BoardEntity toEntity() {
		return BoardEntity.builder()
				.title(title)
				.content(content)
				.author(author)
				.build();
	}
}
