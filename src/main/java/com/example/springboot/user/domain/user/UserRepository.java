package com.example.springboot.user.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
	UserEntity findByEmail(String email);
	boolean existsByEmail(String email);
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE UserEntity u SET u.accessDate = :date WHERE u.email= :email")
	void updateLogin(@Param("date")Date date, @Param("email") String email);
}
