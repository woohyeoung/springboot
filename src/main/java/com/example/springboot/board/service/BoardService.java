package com.example.springboot.board.service;

import com.example.springboot.board.domain.BoardRepository;
import com.example.springboot.board.model.BoardDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BoardService {

	private final BoardRepository boardRepository;

	@Transactional
	public Integer save(BoardDTO boardDTO) {return boardRepository.save(boardDTO.toEntity()).getBoardNo();}

}
