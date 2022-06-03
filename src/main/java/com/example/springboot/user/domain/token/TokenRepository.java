package com.example.springboot.user.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, String> {
	TokenEntity findByEmail(String email);
}
