package com.example.springboot.user.domain.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface TokenRepository extends JpaRepository<TokenEntity, String> {
	TokenEntity findByEmail(String email);
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE TokenEntity t SET t.refreshToken = :token WHERE t.email= :email")
	Integer updateToken(@Param("token") String token, @Param("email") String email);
}
