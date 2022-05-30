package com.example.springboot.board.controller;

import com.example.springboot.board.model.BoardSaveRequestDTO;
import com.example.springboot.board.model.BoardResponseDTO;
import com.example.springboot.board.model.BoardUpdateRequestDTO;
import com.example.springboot.board.service.BoardService;
import com.example.springboot.common.utils.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board")
public class BoardController {

	private final BoardService boardService;

	@PostMapping("/save")
	public Integer save(@RequestBody BoardSaveRequestDTO boardSaveRequestDTO) {return boardService.save(boardSaveRequestDTO);}

	@GetMapping("/list")
	public List<BoardResponseDTO> index() {
		List<BoardResponseDTO> list = boardService.findAllDesc();
		return list;
	}

	@PutMapping("/list/{id}")
	public Integer update(@PathVariable(name = "id") Integer boardNo,
						  @RequestBody BoardUpdateRequestDTO boardUpdateRequestDTO) {
		return boardService.update(boardNo, boardUpdateRequestDTO);
	}

	@GetMapping("/list/{id}")
	public BoardResponseDTO findById(@PathVariable(name = "id") Integer boardNo) {
		return boardService.findById(boardNo);
	}

	@DeleteMapping("/list/{id}")
	public Response delete(@PathVariable(name = "id") Integer boardNo) {
		boardService.delete(boardNo);
		return new Response(2, true, boardNo);
	}
}
