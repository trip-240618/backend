package com.ll.trip.home;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {
	@GetMapping("/hello/get")
	public ResponseEntity<?> helloGet() {
		return ResponseEntity.ok("get mapping");
	}

	@PostMapping("/hello/post")
	public ResponseEntity<?> helloPost(@RequestParam(required = false) String str) {
		if(str == null) return ResponseEntity.ok("post mapping");
		return ResponseEntity.ok("post mapping : " + str);
	}

}
