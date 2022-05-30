package com.example.springboot.board.model;

import com.example.springboot.board.domain.BoardEntity;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class BoardResponseDTO {
	private Integer boardNo;
	private String title, content, author;

	public BoardResponseDTO(Integer boardNo) {
		this.boardNo = boardNo;
	}

	public BoardResponseDTO(BoardEntity entity) {
		this.boardNo = entity.getBoardNo();
		this.title = entity.getTitle();
		this.content = entity.getContent();
		this.author = entity.getAuthor();
	}
}
