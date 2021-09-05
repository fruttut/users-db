package com.azoft.usersdb.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

import javax.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ConvertingExceptionToReplyErrorHandlerTest {
	private final String mockCauseExceptionStackTrace = "Some Internal Exception Stack Trace";
	private ConvertingExceptionToReplyErrorHandler handler;

	@BeforeEach
	public void setup() {
		final StackTraceStringifier mockStringifier = mock(StackTraceStringifier.class);
		when(mockStringifier.stringify(any(Exception.class))).thenReturn(mockCauseExceptionStackTrace);
		handler = new ConvertingExceptionToReplyErrorHandler(mockStringifier);
	}

	@Test
	public void shouldReturnValidationFailureErrorReplyWithoutFullStackTrace_whenConstraintViolationException() {
		final String causeMessage = "Constraint Violation Exception Message";
		final ConstraintViolationException causeException = new ConstraintViolationException(causeMessage, null);
		final ListenerExecutionFailedException ex = new ListenerExecutionFailedException("", causeException);
		testHandlerReply(ex, ConvertingExceptionToReplyErrorHandler.ERROR_REASON_VALIDATION_FAIL, causeMessage);
	}

	@Test
	public void shouldReturnNotFoundErrorReplyWithoutFullStackTrace_whenUserNotFoundException() {
		final int mockUserId = 123;
		final UserNotFoundException causeException = new UserNotFoundException(mockUserId);
		final ListenerExecutionFailedException ex = new ListenerExecutionFailedException("", causeException);
		testHandlerReply(
			ex,
			ConvertingExceptionToReplyErrorHandler.ERROR_REASON_NOT_FOUND,
			causeException.getMessage()
		);
	}

	@Test
	public void shouldReturnInternalErrorReplyWithFullStackTrace_whenAnyOtherException() {
		final Exception causeException = new NullPointerException();
		final ListenerExecutionFailedException ex = new ListenerExecutionFailedException("", causeException);
		testHandlerReply(
			ex,
			ConvertingExceptionToReplyErrorHandler.ERROR_REASON_INTERNAL,
			mockCauseExceptionStackTrace
		);
	}

	private void testHandlerReply(
		final ListenerExecutionFailedException ex,
		final String reason,
		final String message
	) {
		final UserReply reply = handler.handleError(null, null, ex);
		assertFalse(reply.success, "reply should not be successful");
		assertNull(reply.data, "reply should not contain data");
		assertNotNull(reply.error, "reply should contain error");
		assertEquals(
			reason,
			reply.error.reason,
			"reply should have '" + reason + "' as error reason"
		);
		assertEquals(
			message,
			reply.error.message,
			"reply should have '" + message + "' as error message"
		);
	}
}
