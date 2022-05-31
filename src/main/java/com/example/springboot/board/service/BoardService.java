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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BoardService {

	private final BoardRepository boardRepository;

	@Transactional
	public Integer save(BoardSaveRequestDTO boardSaveRequestDTO) {
		try {
			return boardRepository.save(boardSaveRequestDTO.toEntity()).getBoardNo();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	@Transactional(readOnly = true)
	public List<BoardResponseDTO> findAllDesc() {
		try {
			return boardRepository.findAllDesc().stream()
					.map(BoardResponseDTO::new)
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Transactional
	public Integer update(Integer boardNo, BoardUpdateRequestDTO boardUpdateRequestDTO) {
		try {
			BoardEntity boardEntity = boardRepository.findById(boardNo).get();
			boardEntity.update(boardUpdateRequestDTO.getTitle(), boardUpdateRequestDTO.getContent());

			return boardNo;
		} catch (NoSuchElementException ne) {
			ne.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -2;
		}
	}

	@Transactional
	public BoardResponseDTO findById(Integer boardNo) {
		try {
			BoardEntity boardEntity = boardRepository.findById(boardNo).get();

			return new BoardResponseDTO(boardEntity);
		} catch (NoSuchElementException ne) {
			ne.printStackTrace();
			return new BoardResponseDTO(-1);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Transactional
	public Integer delete(Integer boardNo) {
		try {
			BoardEntity boardEntity = boardRepository.findById(boardNo).get();
			boardRepository.delete(boardEntity);

			return boardNo;
		} catch (NoSuchElementException ne) {
			ne.printStackTrace();
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -2;
		}

	}
}
