package com.example.springboot.board.controller;

import com.example.springboot.board.domain.BoardEntity;
import com.example.springboot.board.domain.BoardRepository;
import com.example.springboot.board.model.BoardSaveRequestDTO;
import com.example.springboot.board.model.BoardUpdateRequestDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BoardControllerTest {

	@Autowired private TestRestTemplate restTemplate;

	@Autowired BoardRepository boardRepository;


	@AfterEach
	public void tearDown() {
		boardRepository.deleteAll();
	}

	@Test
	@Transactional
	void 등록된다() throws Exception {
		// given
		String title = "title";
		String content = "content";
		BoardSaveRequestDTO requestDTO = BoardSaveRequestDTO.builder()
				.title(title)
				.content(content)
				.author("author")
				.build();
		String url = "http://localhost:8080/api/board/save";

		// when
		ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestDTO, String.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isGreaterThan("0");

		List<BoardEntity> all = boardRepository.findAll();
		assertThat(all.get(0).getTitle()).isEqualTo(title);
		assertThat(all.get(0).getContent()).isEqualTo(content);
	}

	@Test
	void 수정된다() throws Exception {
		// given
		BoardEntity boardEntity = boardRepository.save(BoardEntity.builder()
				.title("title")
				.content("content")
				.author("author")
				.build());
		Integer id = boardEntity.getBoardNo();
		String t = "title2";
		String c = "content2";

		BoardUpdateRequestDTO boardUpdateRequestDTO = BoardUpdateRequestDTO.builder()
				.title(t)
				.content(c)
				.build();

		String url = "http://localhost:8080/api/board/list/" + id;
		HttpEntity<BoardUpdateRequestDTO> entity = new HttpEntity<>(boardUpdateRequestDTO);

		// when
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);

		// then
		assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(responseEntity.getBody()).isGreaterThan("0");

		List<BoardEntity> all = boardRepository.findAll();
		assertThat(all.get(0).getTitle()).isEqualTo(t);
		assertThat(all.get(0).getContent()).isEqualTo(c);
	}

}