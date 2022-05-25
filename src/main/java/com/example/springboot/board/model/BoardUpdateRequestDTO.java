package com.example.springboot.board.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardUpdateRequestDTO {
	private String title, content;

	@Builder
	public BoardUpdateRequestDTO(String title, String content) {
		this.title = title;
		this.content = content;
	}
}
