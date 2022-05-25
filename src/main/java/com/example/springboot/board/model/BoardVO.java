package com.example.springboot.board.model;

import com.example.springboot.board.domain.Board;
import lombok.Getter;

import java.util.Date;

@Getter
public class BoardVO {
	private Integer boardNo;
	private String title, content, author;
	private Date regDate;
	private int viewCount;

	public BoardVO(Board entity) {
		this.boardNo = entity.getBoardNo();
		this.title = entity.getTitle();
		this.content = entity.getContent();
		this.author = entity.getAuthor();
		this.regDate = entity.getRegDate();
		this.viewCount = entity.getViewCount();
	}
}
