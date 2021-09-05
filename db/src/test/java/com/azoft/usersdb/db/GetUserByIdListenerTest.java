package com.azoft.usersdb.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class GetUserByIdListenerTest {
	private final int mockExistingUserId = 123;
	private final Timestamp mockUserCreationDate = Timestamp.from(Instant.now());
	private UserEntity mockExistingUserEntity;
	private UserService mockService;
	private GetUserByIdListener listener;

	@BeforeEach
	public void setup() throws UserNotFoundException {
		mockExistingUserEntity = new UserEntity(
			"mock login",
			"mock password",
			"mock name",
			"mock surname",
			"mock email"
		);
		ReflectionTestUtils.setField(mockExistingUserEntity, "id", mockExistingUserId, Integer.class);
		ReflectionTestUtils.setField(mockExistingUserEntity, "creationDate", mockUserCreationDate, Timestamp.class);
		mockService = mock(UserService.class);
		when(mockService.getUserById(mockExistingUserId)).thenReturn(mockExistingUserEntity);
		when(mockService.getUserById(not(eq(mockExistingUserId)))).thenAnswer((invocation) -> {
			final int id = invocation.getArgument(0);
			throw new UserNotFoundException(id);
		});
		final Logger mockLogger = mock(Logger.class);
		listener = new GetUserByIdListener(mockService, mockLogger);
	}

	@Test
	public void shouldReturnSuccessReplyWithUserData_whenUserIsPresent() throws UserNotFoundException {
		verify(mockService, never().description("getUserById() should not have been called")).getUserById(any());
		final UserReply reply = assertDoesNotThrow(
			() -> listener.getUserById(mockExistingUserId),
			"should not throw when user is present"
		);
		verify(mockService, times(1).description("getUserById() should have been called once")).getUserById(mockExistingUserId);
		assertTrue(reply.success, "reply should be successful");
		assertNull(reply.error, "reply should not have error");
		assertNotNull(reply.data, "reply should have data");
		assertEquals(reply.data.id, mockExistingUserEntity.id(), "ids should be equal");
		assertEquals(reply.data.login, mockExistingUserEntity.login(), "logins should be equal");
		assertEquals(reply.data.password, mockExistingUserEntity.password(), "passwords should be equal");
		assertEquals(reply.data.name, mockExistingUserEntity.name(), "names should be equal");
		assertEquals(reply.data.surname, mockExistingUserEntity.surname(), "surnames should be equal");
		assertEquals(reply.data.email, mockExistingUserEntity.email(), "emails should be equal");
		assertEquals(reply.data.creationDate, mockExistingUserEntity.creationDate(), "creation dates should be equal");
	}

	@Test
	public void shouldThrowUserNotFoundException_whenUserIsAbsent() throws UserNotFoundException {
		final int mockAbsentUserId = 456;
		verify(mockService, never().description("getUserById() should not have been called")).getUserById(any());
		assertThrows(
			UserNotFoundException.class,
			() -> listener.getUserById(mockAbsentUserId),
			"should throw UserNotFoundException when user is absent"
		);
		verify(mockService, times(1).description("getUserById() should have been called once")).getUserById(mockAbsentUserId);
	}
}
