package com.ll.trip.global.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.ll.trip.global.handler.dto.ErrorResponseDto;
import com.ll.trip.global.handler.exception.NoSuchDataException;
import com.ll.trip.global.handler.exception.PermissionDeniedException;
import com.ll.trip.global.handler.exception.ServerApiException;
import com.ll.trip.global.handler.exception.ServerException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(PermissionDeniedException.class)
	public ResponseEntity<?> handlePermissionDeniedException(PermissionDeniedException e) {
		return ResponseEntity.status(421).body(new ErrorResponseDto(e.getMessage()));
	}

	@ExceptionHandler(NoSuchDataException.class)
	public ResponseEntity<?> handleNoSuchDataException(NoSuchDataException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
	}

	@ExceptionHandler(ServerApiException.class)
	public ResponseEntity<?> handleServerApiException(ServerApiException e) {
		log.error(e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
	}

	@ExceptionHandler(ServerException.class)
	public ResponseEntity<?> handleServerException(ServerException e) {
		log.error(e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
	}
}
