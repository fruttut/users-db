package com.azoft.usersdb.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class UserControllerTest {
	private final int mockUserId = 123;
	private final User mockUser = new User(
		mockUserId,
		"mock login",
		"mock password",
		"mock name",
		"mock surname",
		"mockemail@example.com",
		Timestamp.from(Instant.now())
	);
	private final CreateUserRequest mockCreateUserRequest = new CreateUserRequest(
		"mock login",
		"mock password",
		"mock name",
		"mock surname",
		"mockemail@example.com"
	);
	private final UserError mockError = new UserError("MOCK_ERROR_REASON", "Mock Error Message");
	private final UserReply mockErrorReply = new UserReply(false, mockError, null);
	private final String mockGetUserByIdQueueName = "get-user-by-id";
	private final String mockCreateUserQueueName = "create-user";
	private final HttpStatus mockErrorHttpStatus = HttpStatus.BAD_REQUEST;
	private RabbitTemplate mockTemplate;
	private UserController controller;

	@BeforeEach
	public void setup() {
		mockTemplate = mock(RabbitTemplate.class);

		final Queue mockGetUserByIdQueue = mock(Queue.class);
		when(mockGetUserByIdQueue.getName()).thenReturn(mockGetUserByIdQueueName);

		final Queue mockCreateUserQueue = mock(Queue.class);
		when(mockCreateUserQueue.getName()).thenReturn(mockCreateUserQueueName);

		final ErrorReasonToHttpStatusConverter mockConverter = mock(ErrorReasonToHttpStatusConverter.class);
		when(mockConverter.convert(any())).thenReturn(mockErrorHttpStatus);

		final Logger mockLogger = mock(Logger.class);

		controller = new UserController(
			mockTemplate,
			mockGetUserByIdQueue,
			mockCreateUserQueue,
			mockConverter,
			mockLogger
		);
	}

	@Test
	public void getUserById_shouldReturnUser() {
		final UserReply mockUserReply = new UserReply(true, null, mockUser);
		when(mockTemplate.convertSendAndReceiveAsType(
			eq(mockGetUserByIdQueueName),
			eq(mockUserId),
			any()
		)).thenReturn(mockUserReply);

		verify(
			mockTemplate,
			never().description("convertSendAndReceiveAsType() should not have been called")
		).convertSendAndReceiveAsType(any(String.class), any(Object.class), any());
		final User user = controller.getUserById(mockUserId);
		verify(
			mockTemplate,
			times(1).description("convertSendAndReceiveAsType() should have been called once")
		).convertSendAndReceiveAsType(eq(mockGetUserByIdQueueName), eq(mockUserId), any());
		testUserEquality(mockUser, user);
	}

	@Test
	public void getUserById_shouldThrowResponseStatusException_whenReplyIsNull() {
		when(mockTemplate.convertSendAndReceiveAsType(
			eq(mockGetUserByIdQueueName),
			eq(mockUserId),
			any()
		)).thenReturn(null);

		verify(
			mockTemplate,
			never().description("convertSendAndReceiveAsType() should not have been called")
		).convertSendAndReceiveAsType(any(String.class), any(Object.class), any());
		final ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> controller.getUserById(mockUserId),
			"should throw ResponseStatusException when reply is null"
		);
		verify(
			mockTemplate,
			times(1).description("convertSendAndReceiveAsType() should have been called once")
		).convertSendAndReceiveAsType(eq(mockGetUserByIdQueueName), eq(mockUserId), any());
		assertEquals(HttpStatus.REQUEST_TIMEOUT, ex.getStatus(), "exception should have REQUEST_TIMEOUT HTTP status");
	}

	@Test
	public void getUserById_shouldThrowResponseStatusException_whenErrorReply() {
		when(mockTemplate.convertSendAndReceiveAsType(
			eq(mockGetUserByIdQueueName),
			eq(mockUserId),
			any()
		)).thenReturn(mockErrorReply);

		verify(
			mockTemplate,
			never().description("convertSendAndReceiveAsType() should not have been called")
		).convertSendAndReceiveAsType(any(String.class), any(Object.class), any());
		final ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> controller.getUserById(mockUserId),
			"should throw ResponseStatusException when error reply"
		);
		verify(
			mockTemplate,
			times(1).description("convertSendAndReceiveAsType() should have been called once")
		).convertSendAndReceiveAsType(eq(mockGetUserByIdQueueName), eq(mockUserId), any());
		assertEquals(mockErrorHttpStatus, ex.getStatus(), "exception should have correct HTTP status");
		assertEquals(mockError.message, ex.getReason(), "exception should have correct reason");
	}
	
	@Test
	public void createUser_shouldReturnNewUser() {
		final UserReply mockUserReply = new UserReply(true, null, mockUser);
		when(mockTemplate.convertSendAndReceiveAsType(
			eq(mockCreateUserQueueName),
			eq(mockCreateUserRequest),
			any()
		)).thenReturn(mockUserReply);

		verify(
			mockTemplate,
			never().description("convertSendAndReceiveAsType() should not have been called")
		).convertSendAndReceiveAsType(any(String.class), any(Object.class), any());
		final User user = controller.createUser(mockCreateUserRequest);
		verify(
			mockTemplate,
			times(1).description("convertSendAndReceiveAsType() should have been called once")
		).convertSendAndReceiveAsType(eq(mockCreateUserQueueName), eq(mockCreateUserRequest), any());
		testUserEquality(mockUser, user);
	}

	@Test
	public void createUser_shouldThrowResponseStatusException_whenReplyIsNull() {
		when(mockTemplate.convertSendAndReceiveAsType(
			eq(mockCreateUserQueueName),
			eq(mockCreateUserRequest),
			any()
		)).thenReturn(null);

		verify(
			mockTemplate,
			never().description("convertSendAndReceiveAsType() should not have been called")
		).convertSendAndReceiveAsType(any(String.class), any(Object.class), any());
		final ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> controller.createUser(mockCreateUserRequest),
			"should throw ResponseStatusException when reply is null"
		);
		verify(
			mockTemplate,
			times(1).description("convertSendAndReceiveAsType() should have been called once")
		).convertSendAndReceiveAsType(eq(mockCreateUserQueueName), eq(mockCreateUserRequest), any());
		assertEquals(HttpStatus.REQUEST_TIMEOUT, ex.getStatus(), "exception should have REQUEST_TIMEOUT HTTP status");
	}

	@Test
	public void createUser_shouldThrowResponseStatusException_whenErrorReply() {
		when(mockTemplate.convertSendAndReceiveAsType(
			eq(mockCreateUserQueueName),
			eq(mockCreateUserRequest),
			any()
		)).thenReturn(mockErrorReply);

		verify(
			mockTemplate,
			never().description("convertSendAndReceiveAsType() should not have been called")
		).convertSendAndReceiveAsType(any(String.class), any(Object.class), any());
		final ResponseStatusException ex = assertThrows(
			ResponseStatusException.class,
			() -> controller.createUser(mockCreateUserRequest),
			"should throw ResponseStatusException when error reply"
		);
		verify(
			mockTemplate,
			times(1).description("convertSendAndReceiveAsType() should have been called once")
		).convertSendAndReceiveAsType(eq(mockCreateUserQueueName), eq(mockCreateUserRequest), any());
		assertEquals(mockErrorHttpStatus, ex.getStatus(), "exception should have correct HTTP status");
		assertEquals(mockError.message, ex.getReason(), "exception should have correct reason");
	}
	
	private void testUserEquality(final User expected, final User actual) {
		assertEquals(expected.id, actual.id, "ids should be equal");
		assertEquals(expected.login, actual.login, "logins should be equal");
		assertEquals(expected.password, actual.password, "passwords should be equal");
		assertEquals(expected.name, actual.name, "names should be equal");
		assertEquals(expected.surname, actual.surname, "surnames should be equal");
		assertEquals(expected.email, actual.email, "emails should be equal");
		assertEquals(expected.creationDate, actual.creationDate, "creation dates should be equal");
	}
}
