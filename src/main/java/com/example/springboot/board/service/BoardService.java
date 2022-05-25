package com.example.springboot.board.service;

import com.example.springboot.board.domain.BoardEntity;
import com.example.springboot.board.domain.BoardRepository;
import com.example.springboot.board.model.BoardSaveRequestDTO;
import com.example.springboot.board.model.BoardResponseDTO;
import com.example.springboot.board.model.BoardUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {

	private final BoardRepository boardRepository;

	@Transactional
	public Integer save(BoardSaveRequestDTO boardSaveRequestDTO) {return boardRepository.save(boardSaveRequestDTO.toEntity()).getBoardNo();}

	@Transactional(readOnly = true)
	public List<BoardResponseDTO> findAllDesc() {
		return boardRepository.findAllDesc().stream()
				.map(BoardResponseDTO::new)
				.collect(Collectors.toList());
	}

	@Transactional
	public Integer update(Integer boardNo, BoardUpdateRequestDTO boardUpdateRequestDTO) {
		BoardEntity boardEntity = boardRepository.findById(boardNo)
				.orElseThrow(()-> new IllegalArgumentException("해당 게시물이 없습니다. boardNo = " + boardNo));
		boardEntity.update(boardUpdateRequestDTO.getTitle(), boardUpdateRequestDTO.getContent());
		return boardNo;
	}

	@Transactional
	public BoardResponseDTO findById(Integer boardNo) {
		BoardEntity boardEntity = boardRepository.findById(boardNo)
				.orElseThrow(()-> new IllegalArgumentException("해당 게시물이 없습니다. boardNo = " + boardNo));
		return new BoardResponseDTO(boardEntity);
	}

	@Transactional
	public void delete(Integer boardNo) {
		BoardEntity boardEntity = boardRepository.findById(boardNo)
				.orElseThrow(()-> new IllegalArgumentException("해당 게시물이 없습니다. boardNo = " + boardNo));
		boardRepository.delete(boardEntity);
	}
}
