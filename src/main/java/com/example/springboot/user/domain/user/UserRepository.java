package com.example.springboot.user.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

	Boolean existsByEmail(String email);
	UserEntity findByEmail(String email);
}
