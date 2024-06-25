package com.ll.trip.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ll.trip.healthCheck.entity.TestEntity;
import com.ll.trip.healthCheck.repository.TestRepository;

import lombok.RequiredArgsConstructor;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor
@SpringBootTest
public class DatabaseConnectionTest {

	private final TestRepository testRepository;
	private final String testValue = "trip_database";

	@Test
	@Transactional
	public void testConnecion() {
		if(testRepository.count() == 0) {
			TestEntity test = new TestEntity();
			test.setValue(testValue);
			testRepository.save(test);
		}

		Optional<TestEntity> optTest = testRepository.findById(1L);
		assertThat(optTest).isPresent();
		optTest.ifPresent(t -> assertThat(t.getValue()).isEqualTo(testValue));
	}
}
