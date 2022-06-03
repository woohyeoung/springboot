package com.example.springboot.user.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<TokenEntity, String> {
	TokenEntity findByEmail(String email);
	@Modifying(clearAutomatically = true)
	@Query("UPDATE TokenEntity t SET t.accessToken=:accessToken WHERE t.email=:email")
	String updateByAccessToken(String accessToken);
}
