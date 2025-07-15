package com.ll.trip.global.handler;

import com.ll.trip.global.handler.dto.ErrorResponseDto;
import com.ll.trip.global.handler.exception.NoSuchDataException;
import com.ll.trip.global.handler.exception.PermissionDeniedException;
import com.ll.trip.global.handler.exception.ServerApiException;
import com.ll.trip.global.handler.exception.ServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.concurrent.ExecutionException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(PermissionDeniedException.class)
	public ResponseEntity<ErrorResponseDto> handlePermissionDeniedException(PermissionDeniedException e) {
		return ResponseEntity.status(421).body(new ErrorResponseDto(e.getMessage()));
	}

	@ExceptionHandler(NoSuchDataException.class)
	public ResponseEntity<ErrorResponseDto> handleNoSuchDataException(NoSuchDataException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto(e.getMessage()));
	}

	@ExceptionHandler(ServerApiException.class)
	public ResponseEntity<ErrorResponseDto> handleServerApiException(ServerApiException e) {
		log.error(e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto(e.getMessage()));
	}

	@ExceptionHandler(ServerException.class)
	public ResponseEntity<ErrorResponseDto> handleServerException(ServerException e) {
		log.error(e.getMessage()
				//,e              //#stack trace 까지 보고싶다면
		);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponseDto("서버 내부 오류로 인해 요청을 처리할 수 없습니다. 잠시 후 다시 시도해주세요."));
	}

	@ExceptionHandler(ExecutionException.class)
	public void handleExecutionException(ExecutionException e) {
		Throwable cause = e.getCause(); // 원본 예외 추출

		if (cause != null) {
			log.error("ExecutionException occurred: {}", cause.getMessage(), cause);
		} else {
			log.error("ExecutionException with no cause: {}", e.getMessage(), e);
		}
	}
}
