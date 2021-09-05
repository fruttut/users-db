package com.azoft.usersdb.db;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;

import javax.validation.ConstraintViolationException;
import java.util.AbstractMap;
import java.util.Map;

public final class ConvertingExceptionToReplyErrorHandler implements RabbitListenerErrorHandler {
	public static final String ERROR_REASON_VALIDATION_FAIL = "VALIDATION_FAIL";
	public static final String ERROR_REASON_NOT_FOUND = "NOT_FOUND";
	public static final String ERROR_REASON_INTERNAL = "INTERNAL";

	public static final ReplyProperties FALLBACK_REPLY_PROPERTIES = new ReplyProperties(ERROR_REASON_INTERNAL, true);

	private static final Map<Class<? extends Throwable>, ReplyProperties> exceptionTypeToReplyPropertiesMapping = Map.ofEntries(
		new AbstractMap.SimpleImmutableEntry<>(
			ConstraintViolationException.class,
			new ReplyProperties(ERROR_REASON_VALIDATION_FAIL)
		),
		new AbstractMap.SimpleImmutableEntry<>(
			UserNotFoundException.class,
			new ReplyProperties(ERROR_REASON_NOT_FOUND)
		)
	);

	public static final class ReplyProperties {
		public final String reason;
		public final boolean fullStackTrace;

		public ReplyProperties(final String reason) {
			this(reason, false);
		}

		public ReplyProperties(final String reason, final boolean fullStackTrace) {
			this.reason = reason;
			this.fullStackTrace = fullStackTrace;
		}

		@Override
		public String toString() {
			return String.format(
				"reason: %s; fullStackTrace: %b",
				reason,
				fullStackTrace
			);
		}
	}

	private final StackTraceStringifier stackTraceStringifier;

	public ConvertingExceptionToReplyErrorHandler(final StackTraceStringifier stackTraceStringifier) {
		this.stackTraceStringifier = stackTraceStringifier;
	}

	@Override
	public UserReply handleError(
		final Message rawMessage,
		final org.springframework.messaging.Message<?> message,
		final ListenerExecutionFailedException exception
	) {
		final Throwable cause = exception.getCause();
		final ReplyProperties replyProperties = exceptionTypeToReplyPropertiesMapping.getOrDefault(
			cause.getClass(),
			FALLBACK_REPLY_PROPERTIES
		);
		final String errorMessage = replyProperties.fullStackTrace
			? stackTraceStringifier.stringify(exception)
			: cause.getMessage();
		return UserReply.errorReply(
			new UserError(
				replyProperties.reason,
				errorMessage
			)
		);
	}
}
