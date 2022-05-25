package com.example.springboot.board.model;

import com.example.springboot.board.domain.BoardEntity;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardListResponseDTO {
	private Integer boardNo;
	private String title, author;
	private LocalDateTime modifiedDate;

	public BoardListResponseDTO(BoardEntity boardEntity) {
		this.boardNo = boardEntity.getBoardNo();
		this.title = boardEntity.getTitle();
		this.author = boardEntity.getAuthor();
		this.modifiedDate = boardEntity.getModifiedDate();
	}
}
