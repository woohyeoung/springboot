package com.example.springboot.board.service;

import com.example.springboot.board.domain.BoardEntity;
import com.example.springboot.board.domain.BoardRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BoardEntityServiceTest {


	@Autowired BoardRepository boardRepository;

	@AfterEach
	public void cleanUp() {
		boardRepository.deleteAll();
	}

	@Test
	void 게시글등록_불러오기테스트() {
		// given
		String title = "test board";
		String content = "test content";
		String author = "test author";

		boardRepository.save(BoardEntity.builder()
						.title(title)
						.content(content)
						.author(author)
						.build());

		// when
		List<BoardEntity> boardEntityList = boardRepository.findAll();

		// then
		BoardEntity boardEntity = boardEntityList.get(0);
		assertThat(boardEntity.getTitle()).isEqualTo(title);
		assertThat(boardEntity.getContent()).isEqualTo(content);
		assertThat(boardEntity.getAuthor()).isEqualTo(author);
	}

	@Test
	void BaseTimeEntity_등록() {
		// given
		LocalDateTime now = LocalDateTime.of(2022,5, 25, 0, 0, 0);
		boardRepository.save(BoardEntity.builder()
				.title("title")
				.content("content")
				.author("author")
				.build());

		// when
		List<BoardEntity> boardEntityList = boardRepository.findAll();

		// then
		BoardEntity boardEntity = boardEntityList.get(0);
		System.out.println(boardEntity.getCreateDate());
		System.out.println(boardEntity.getModifiedDate());

		assertThat(boardEntity.getCreateDate()).isAfter(now);
		assertThat(boardEntity.getModifiedDate()).isAfter(now);
	}
}