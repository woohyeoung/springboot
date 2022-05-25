package com.example.springboot.board.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<BoardEntity, Integer> {

	@Query("SELECT p FROM BoardEntity p ORDER BY p.boardNo DESC")
	List<BoardEntity> findAllDesc();
}
