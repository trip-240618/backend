package com.ll.trip.global.handler.exception;

public class ServerApiException extends RuntimeException{
	// 기본 생성자
	public ServerApiException() {
		super("server error");  // 기본 예외 메시지 설정
	}

	// 메시지를 받을 수 있는 생성자
	public ServerApiException(String message) {
		super(message);  // 커스텀 메시지를 받을 때 사용할 생성자
	}

	// 메시지와 원인 (Throwable)을 받을 수 있는 생성자
	public ServerApiException(String message, Throwable cause) {
		super(message, cause);  // 예외 메시지와 원인(다른 예외)을 함께 전달할 수 있음
	}

	// 원인만 받을 수 있는 생성자
	public ServerApiException(Throwable cause) {
		super(cause);  // 다른 예외의 원인만 전달받을 때 사용
	}
}
