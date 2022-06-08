package com.example.springboot.board.controller;

import com.example.springboot.board.model.BoardSaveRequestDTO;
import com.example.springboot.board.model.BoardResponseDTO;
import com.example.springboot.board.model.BoardUpdateRequestDTO;
import com.example.springboot.board.service.BoardService;
import com.example.springboot.common.response.Payload;
import com.example.springboot.common.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api_user/board")
public class BoardController {

	private final Logger logger = LoggerFactory.getLogger(BoardController.class);
	private final BoardService boardService;

	@PostMapping("/save")
	public ResponseDTO save(@RequestBody BoardSaveRequestDTO boardSaveRequestDTO) {
		logger.info("BoardController save method ...");
		Integer result = boardService.save(boardSaveRequestDTO);

		if(result == -1) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR,
											Payload.SERVER_ERROR + "BoardController save()");

		return new ResponseDTO().of(HttpStatus.OK, Payload.BOARD_SAVE_OK);
	}

	@GetMapping("/list")
	public ResponseDTO index() {
		logger.info("BoardController index method ...");
		List<BoardResponseDTO> list = boardService.findAllDesc();

		if(list.size() < 1) return new ResponseDTO().fail(HttpStatus.BAD_REQUEST, Payload.BOARD_LIST_FAIL);
		if(list == null) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR,
				Payload.SERVER_ERROR + "BoardController index()");

		return new ResponseDTO().of(HttpStatus.OK, Payload.BOARD_LIST_OK, list);
	}

	@PutMapping("/list/{id}")
	public ResponseDTO update(@PathVariable(name = "id") Integer boardNo,
							  @RequestBody BoardUpdateRequestDTO boardUpdateRequestDTO) {
		logger.info("BoardController update method ...");
		Integer result = boardService.update(boardNo, boardUpdateRequestDTO);

		if(result == -1) return new ResponseDTO().fail(HttpStatus.BAD_REQUEST, Payload.BOARD_UPDATE_FAIL);
		if(result == -2) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR, Payload.BOARD_UPDATE_FAIL + "BoardController update()");

		return new ResponseDTO().of(HttpStatus.OK, Payload.BOARD_UPDATE_OK, boardNo);
	}

	@GetMapping("/list/{id}")
	public ResponseDTO findById(@PathVariable(name = "id") Integer boardNo) {
		logger.info("BoardController findById method ...");
		BoardResponseDTO result = boardService.findById(boardNo);

		if(result.getBoardNo() == -1) return new ResponseDTO().fail(HttpStatus.BAD_REQUEST, Payload.BOARD_READ_FAIL);
		if(result == null) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR, Payload.BOARD_READ_FAIL + "BoardController findById()");

		return new ResponseDTO().of(HttpStatus.OK, Payload.BOARD_READ_OK, result);
	}

	@DeleteMapping("/list/{id}")
	public ResponseDTO delete(@PathVariable(name = "id") Integer boardNo) {
		logger.info("BoardController delete method ...");
		Integer result = boardService.delete(boardNo);

		if(result == -1) return new ResponseDTO().fail(HttpStatus.BAD_REQUEST, Payload.BOARD_DELETE_FAIL);
		if(result == -2) return new ResponseDTO().fail(HttpStatus.INTERNAL_SERVER_ERROR, Payload.BOARD_DELETE_FAIL + "BoardController delete()");

		return new ResponseDTO().of(HttpStatus.OK, Payload.BOARD_DELETE_OK, result);
	}
}
