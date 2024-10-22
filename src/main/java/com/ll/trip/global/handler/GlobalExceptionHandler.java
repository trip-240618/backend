package com.ll.trip.global.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ll.trip.global.handler.dto.ErrorResponseDto;
import com.ll.trip.global.handler.exception.PermissionDeniedException;
import com.ll.trip.global.handler.exception.ServerApiException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(PermissionDeniedException.class)
	public ResponseEntity<?> handlePermissionDeniedException(PermissionDeniedException e) {
		// 400 상태 코드와 함께 예외 메시지를 반환
		return ResponseEntity.status(421).body(new ErrorResponseDto(e.getMessage()));
	}

	@ExceptionHandler(ServerApiException.class)
	public void handleServerApiException(ServerApiException e) {
		// 400 상태 코드와 함께 예외 메시지를 반환
		log.error(e.getMessage());
	}

}
