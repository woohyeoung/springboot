package com.example.springboot.common.security.handler;

import com.example.springboot.common.response.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class ResponseHandler {
	private static final Logger logger = LoggerFactory.getLogger(ResponseHandler.class);
	private final ObjectMapper objectMapper = new ObjectMapper();

	public String convertResult(HttpStatus httpStatus, String message) {
		logger.info("ResponseHandler - convertResult() ...");
		String result = "메시지 변환 에러";
		try {
			result = objectMapper.writeValueAsString(ResponseDTO.builder()
																.status(httpStatus)
																.message(message)
																.build());
		} catch (IOException ie) {
			logger.error("입력 값을 읽어오지 못했습니다. ResponseHandler - convertResult()", ie);
		} catch (Exception e) {
			logger.error("SERVER ERROR ResponseHandler - convertResult()", e);
		}
		return result;
	}
}
