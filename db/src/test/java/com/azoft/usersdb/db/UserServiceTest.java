package com.azoft.usersdb.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class UserServiceTest {
	private final int existingUserMockId = 123;
	private UserEntity existingUserMockEntity;
	private UserRepository mockRepo;
	private UserService service;

	@BeforeEach
	public void setup() {
		existingUserMockEntity = new UserEntity(
			"mock login",
			"mock password",
			"mock name",
			"mock surname",
			"mockemail@example.com"
		);
		final Timestamp mockUserCreationDate = Timestamp.from(Instant.now());
		ReflectionTestUtils.setField(existingUserMockEntity, "id", existingUserMockId, Integer.class);
		ReflectionTestUtils.setField(existingUserMockEntity, "creationDate", mockUserCreationDate, Timestamp.class);

		mockRepo = mock(UserRepository.class);
		when(mockRepo.findById(existingUserMockId)).thenReturn(Optional.of(existingUserMockEntity));
		when(mockRepo.findById(not(eq(existingUserMockId)))).thenReturn(Optional.empty());

		final Logger mockLogger = mock(Logger.class);

		service = new UserService(mockRepo, mockLogger);
	}

	@Test
	public void getUserById_shouldReturnUserEntity_whenUserIsPresent() {
		verify(mockRepo, never().description("findById should not have been called")).findById(any());
		final UserEntity foundEntity = assertDoesNotThrow(
			() -> service.getUserById(existingUserMockId),
			"should not throw when user is present"
		);
		verify(mockRepo, times(1).description("findById should have been called once")).findById(existingUserMockId);
		assertEquals(existingUserMockEntity.id(), foundEntity.id(), "ids should be equal");
		assertEquals(existingUserMockEntity.login(), foundEntity.login(), "logins should be equal");
		assertEquals(existingUserMockEntity.password(), foundEntity.password(), "passwords should be equal");
		assertEquals(existingUserMockEntity.name(), foundEntity.name(), "names should be equal");
		assertEquals(existingUserMockEntity.surname(), foundEntity.surname(), "surnames should be equal");
		assertEquals(existingUserMockEntity.email(), foundEntity.email(), "emails should be equal");
		assertEquals(existingUserMockEntity.creationDate(), foundEntity.creationDate(), "creation dates should be equal");
	}

	@Test
	public void getUserById_shouldThrowUserNotFoundException_whenUserIsAbsent() {
		final int absentUserMockId = 456;
		verify(mockRepo, never().description("findById should not have been called")).findById(any());
		assertThrows(
			UserNotFoundException.class,
			() -> service.getUserById(absentUserMockId),
			"should throw UserNotFoundException when user is absent"
		);
		verify(mockRepo, times(1).description("findById should have been called once")).findById(absentUserMockId);
	}

	@Test
	public void createUser_shouldReturnNewUserEntity() {

	}
}
