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
	private final int mockExistingUserId = 123;
	private final int mockGeneratedUserId = 999;
	private final Timestamp mockUserCreationDate = Timestamp.from(Instant.now());
	private UserEntity mockExistingUserEntity;
	private UserRepository mockRepo;
	private UserService service;

	@BeforeEach
	public void setup() {
		mockExistingUserEntity = new UserEntity(
			"mock login",
			"mock password",
			"mock name",
			"mock surname",
			"mockemail@example.com"
		);
		ReflectionTestUtils.setField(mockExistingUserEntity, "id", mockExistingUserId, Integer.class);
		ReflectionTestUtils.setField(mockExistingUserEntity, "creationDate", mockUserCreationDate, Timestamp.class);

		mockRepo = mock(UserRepository.class);
		when(mockRepo.findById(mockExistingUserId)).thenReturn(Optional.of(mockExistingUserEntity));
		when(mockRepo.findById(not(eq(mockExistingUserId)))).thenReturn(Optional.empty());
		when(mockRepo.save(any())).thenAnswer((invocation) -> {
			final UserEntity entity = invocation.getArgument(0);
			ReflectionTestUtils.setField(entity, "id", mockGeneratedUserId, Integer.class);
			ReflectionTestUtils.setField(entity, "creationDate", mockUserCreationDate, Timestamp.class);
			return entity;
		});

		final Logger mockLogger = mock(Logger.class);

		service = new UserService(mockRepo, mockLogger);
	}

	@Test
	public void getUserById_shouldReturnUserEntity_whenUserIsPresent() {
		verify(mockRepo, never().description("findById() should not have been called")).findById(any());
		final UserEntity foundEntity = assertDoesNotThrow(
			() -> service.getUserById(mockExistingUserId),
			"should not throw when user is present"
		);
		verify(mockRepo, times(1).description("findById() should have been called once")).findById(mockExistingUserId);
		assertEquals(mockExistingUserEntity.id(), foundEntity.id(), "ids should be equal");
		assertEquals(mockExistingUserEntity.login(), foundEntity.login(), "logins should be equal");
		assertEquals(mockExistingUserEntity.password(), foundEntity.password(), "passwords should be equal");
		assertEquals(mockExistingUserEntity.name(), foundEntity.name(), "names should be equal");
		assertEquals(mockExistingUserEntity.surname(), foundEntity.surname(), "surnames should be equal");
		assertEquals(mockExistingUserEntity.email(), foundEntity.email(), "emails should be equal");
		assertEquals(mockExistingUserEntity.creationDate(), foundEntity.creationDate(), "creation dates should be equal");
	}

	@Test
	public void getUserById_shouldThrowUserNotFoundException_whenUserIsAbsent() {
		final int absentUserMockId = 456;
		verify(mockRepo, never().description("findById() should not have been called")).findById(any());
		assertThrows(
			UserNotFoundException.class,
			() -> service.getUserById(absentUserMockId),
			"should throw UserNotFoundException when user is absent"
		);
		verify(mockRepo, times(1).description("findById() should have been called once")).findById(absentUserMockId);
	}

	@Test
	public void createUser_shouldReturnNewUserEntity() {
		final String mockLogin = "mock login";
		final String mockPassword = "mock password";
		final String mockName = "mock name";
		final String mockSurname = "mock surname";
		final String mockEmail = "mockemail@example.com";

		verify(mockRepo, never().description("save() should not have been called")).save(any());
		final UserEntity entity = service.createUser(mockLogin, mockPassword, mockName, mockSurname, mockEmail);
		verify(mockRepo, times(1).description("save() should have been called once")).save(any());
		assertEquals(mockGeneratedUserId, entity.id(), "ids should be equal");
		assertEquals(mockLogin, entity.login(), "logins should be equal");
		assertEquals(mockPassword, entity.password(), "passwords should be equal");
		assertEquals(mockName, entity.name(), "names should be equal");
		assertEquals(mockSurname, entity.surname(), "surnames should be equal");
		assertEquals(mockEmail, entity.email(), "emails should be equal");
		assertEquals(mockUserCreationDate, entity.creationDate(), "creation dates should be equal");
	}
}
