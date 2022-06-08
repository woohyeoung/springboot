package com.example.springboot.common.security.handler;

import com.example.springboot.common.response.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
public class ResponseHandler {
	private final ObjectMapper objectMapper = new ObjectMapper();

	public String convertResult(HttpStatus httpStatus, String message) {
		log.info("ResponseHandler - convertResult() ...");
		String result = "메시지 변환 에러";
		try {
			result = objectMapper.writeValueAsString(ResponseDTO.builder()
																.status(httpStatus)
																.message(message)
																.build());
		} catch (IOException ie) {
			log.error("입력 값을 읽어오지 못했습니다. ResponseHandler - convertResult()", ie);
		} catch (Exception e) {
			log.error("SERVER ERROR ResponseHandler - convertResult()", e);
		}
		return result;
	}
}
