package com.ll.trip.global.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {
	// @ExceptionHandler(NoSuchElementException.class)
	// public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException e) {
	// 	// 404 상태 코드와 함께 예외 메시지를 반환
	// 	return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
	// }
	//
	// @ExceptionHandler(IllegalArgumentException.class)
	// public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
	// 	// 400 상태 코드와 함께 예외 메시지를 반환
	// 	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
	// }
}
