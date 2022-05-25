package com.example.springboot.board.service;

import com.example.springboot.board.domain.BoardRepository;
import com.example.springboot.board.model.BoardRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BoardService {

	private final BoardRepository boardRepository;

	@Transactional
	public Integer save(BoardRequestDTO boardRequestDTO) {return boardRepository.save(boardRequestDTO.toEntity()).getBoardNo();}

}
