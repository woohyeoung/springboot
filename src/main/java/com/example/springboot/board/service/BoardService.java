package com.example.springboot.board.service;

import com.example.springboot.board.domain.BoardEntity;
import com.example.springboot.board.domain.BoardRepository;
import com.example.springboot.board.model.BoardSaveRequestDTO;
import com.example.springboot.board.model.BoardResponseDTO;
import com.example.springboot.board.model.BoardUpdateRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class BoardService {
	private final BoardRepository boardRepository;

	@Transactional
	public Integer save(BoardSaveRequestDTO boardSaveRequestDTO) {
		log.info("BoardService - save() ...");
		try {
			return boardRepository.save(boardSaveRequestDTO.toEntity()).getBoardNo();
		} catch (Exception e) {
			log.error("SERVER ERROR BoardService - save()", e);
			return -1;
		}
	}

	@Transactional(readOnly = true)
	public List<BoardResponseDTO> findAllDesc() {
		log.info("BoardService - findAllDesc() ...");
		try {
			return boardRepository.findAllDesc().stream()
					.map(BoardResponseDTO::new)
					.collect(Collectors.toList());
		} catch (Exception e) {
			log.error("SERVER ERROR BoardService - findAllDesc()", e);
			return null;
		}
	}

	@Transactional
	public Integer update(Integer boardNo, BoardUpdateRequestDTO boardUpdateRequestDTO) {
		log.info("BoardService - update() ...");
		try {
			BoardEntity boardEntity = boardRepository.findById(boardNo).get();
			boardEntity.update(boardUpdateRequestDTO.getTitle(), boardUpdateRequestDTO.getContent());

			return boardNo;
		} catch (NoSuchElementException ne) {
			log.error("값이 들어갈 공간이 없습니다. BoardService - update()", ne);
			return -1;
		} catch (Exception e) {
			log.error("SERVER ERROR BoardService - update()", e);
			return -2;
		}
	}

	@Transactional
	public BoardResponseDTO findById(Integer boardNo) {
		log.info("BoardService - findById() ...");
		try {
			BoardEntity boardEntity = boardRepository.findById(boardNo).get();

			return new BoardResponseDTO(boardEntity);
		} catch (NoSuchElementException ne) {
			log.error("값이 들어갈 공간이 없습니다. BoardService - update()", ne);
			return new BoardResponseDTO(-1);
		} catch (Exception e) {
			log.error("SERVER ERROR BoardService - update()", e);
			return new BoardResponseDTO(-2);
		}
	}

	@Transactional
	public Integer delete(Integer boardNo) {
		log.info("BoardService - delete() ...");
		try {
			BoardEntity boardEntity = boardRepository.findById(boardNo).get();
			boardRepository.delete(boardEntity);

			return boardNo;
		} catch (NoSuchElementException ne) {
			log.error("값이 들어갈 공간이 없습니다. BoardService - update()", ne);
			return -1;
		} catch (Exception e) {
			log.error("SERVER ERROR BoardService - update()", e);
			return -2;
		}

	}
}
