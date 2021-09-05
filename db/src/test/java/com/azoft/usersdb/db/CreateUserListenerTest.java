package com.azoft.usersdb.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class CreateUserListenerTest {
	private final int mockGeneratedUserId = 999;
	private final Timestamp mockGeneratedUserCreationDate = Timestamp.from(Instant.now());
	private UserService mockService;
	private CreateUserListener listener;

	@BeforeEach
	public void setup() {
		mockService = mock(UserService.class);
		when(mockService.createUser(any(), any(), any(), any(), any())).thenAnswer((invocation) -> {
			final UserEntity entity = new UserEntity(
				invocation.getArgument(0),
				invocation.getArgument(1),
				invocation.getArgument(2),
				invocation.getArgument(3),
				invocation.getArgument(4)
			);
			ReflectionTestUtils.setField(entity, "id", mockGeneratedUserId, Integer.class);
			ReflectionTestUtils.setField(entity, "creationDate", mockGeneratedUserCreationDate, Timestamp.class);
			return entity;
		});
		final Logger mockLogger = mock(Logger.class);
		listener = new CreateUserListener(mockService, mockLogger);
	}

	@Test
	public void shouldReturnSuccessReplyWithUserData() {
		final String mockLogin = "mock login";
		final String mockPassword = "mock password";
		final String mockName = "mock name";
		final String mockSurname = "mock surname";
		final String mockEmail = "mockemail@example.com";
		final CreateUserRequest request = new CreateUserRequest(
			mockLogin,
			mockPassword,
			mockName,
			mockSurname,
			mockEmail
		);

		verify(mockService, never().description("createUser() should not have been called")).createUser(
			any(), any(), any(), any(), any()
		);
		final UserReply reply = listener.createUser(request);
		verify(mockService, times(1).description("createUser() should have been called once")).createUser(
			mockLogin,
			mockPassword,
			mockName,
			mockSurname,
			mockEmail
		);
		assertTrue(reply.success, "reply should be successful");
		assertNull(reply.error, "reply should not have error");
		assertNotNull(reply.data, "reply should have data");
		assertEquals(reply.data.id, mockGeneratedUserId, "ids should be equal");
		assertEquals(reply.data.login, mockLogin, "logins should be equal");
		assertEquals(reply.data.password, mockPassword, "passwords should be equal");
		assertEquals(reply.data.name, mockName, "names should be equal");
		assertEquals(reply.data.surname, mockSurname, "surnames should be equal");
		assertEquals(reply.data.email, mockEmail, "emails should be equal");
		assertEquals(reply.data.creationDate, mockGeneratedUserCreationDate, "creation dates should be equal");
	}
}
