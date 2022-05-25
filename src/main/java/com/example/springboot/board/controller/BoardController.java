package com.example.springboot.board.controller;

import com.example.springboot.board.model.BoardRequestDTO;
import com.example.springboot.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BoardController {

	private final BoardService boardService;

	@PostMapping("/board/save_from")
	public Integer save(@RequestBody BoardRequestDTO boardRequestDTO) {return boardService.save(boardRequestDTO);}

}
