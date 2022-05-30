package com.example.springboot.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Response {
	private int code;
	private boolean isSuccess;
	private Object value;
}
