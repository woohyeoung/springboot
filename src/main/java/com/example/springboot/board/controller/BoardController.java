package com.example.springboot.board.controller;

import com.example.springboot.board.model.BoardSaveRequestDTO;
import com.example.springboot.board.model.BoardResponseDTO;
import com.example.springboot.board.model.BoardUpdateRequestDTO;
import com.example.springboot.board.service.BoardService;
import com.example.springboot.common.utils.Response;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board")
public class BoardController {

	private final Logger logger = LoggerFactory.getLogger(BoardController.class);
	private final BoardService boardService;

	@PostMapping("/save")
	public Response save(@RequestBody BoardSaveRequestDTO boardSaveRequestDTO) {
		logger.info("BoardController save method ...");
		return new Response(2, true, boardService.save(boardSaveRequestDTO));
	}

	@GetMapping("/list")
	public Response index() {
		logger.info("BoardController index method ...");
		List<BoardResponseDTO> list = boardService.findAllDesc();
		return new Response(2, true, list);
	}

	@PutMapping("/list/{id}")
	public Response update(@PathVariable(name = "id") Integer boardNo,
						  @RequestBody BoardUpdateRequestDTO boardUpdateRequestDTO) {
		logger.info("BoardController update method ...");
		return new Response(2, true, boardService.update(boardNo, boardUpdateRequestDTO));
	}

	@GetMapping("/list/{id}")
	public Response findById(@PathVariable(name = "id") Integer boardNo) {
		logger.info("BoardController findById method ...");
		return new Response(2, true, boardService.findById(boardNo));
	}

	@DeleteMapping("/list/{id}")
	public Response delete(@PathVariable(name = "id") Integer boardNo) {
		logger.info("BoardController delete method ...");
		boardService.delete(boardNo);
		return new Response(2, true, boardNo);
	}
}
