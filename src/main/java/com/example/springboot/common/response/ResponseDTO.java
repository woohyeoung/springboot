package com.example.springboot.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ResponseDTO {
	private HttpStatus status;
	private String message;
	private Object value;

	public ResponseDTO(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}

	public ResponseDTO of(HttpStatus status, String message, Object value) {
		return new ResponseDTO(status, message, value);
	}

	public ResponseDTO of(HttpStatus status, String message) {
		return new ResponseDTO(status, message);
	}

	public ResponseDTO fail(HttpStatus status, String message) {
		return new ResponseDTO(status, message, null);
	}
}
