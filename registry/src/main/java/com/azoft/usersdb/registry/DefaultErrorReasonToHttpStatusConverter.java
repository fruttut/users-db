package com.azoft.usersdb.registry;

import org.springframework.http.HttpStatus;

import java.util.AbstractMap;
import java.util.Map;

public final class DefaultErrorReasonToHttpStatusConverter implements ErrorReasonToHttpStatusConverter {
	public static final String ERROR_REASON_VALIDATION_FAIL = "VALIDATION_FAIL";
	public static final String ERROR_REASON_NOT_FOUND = "NOT_FOUND";
	public static final String ERROR_REASON_INTERNAL = "INTERNAL";

	public static final HttpStatus DEFAULT_FALLBACK_HTTP_STATUS = HttpStatus.INTERNAL_SERVER_ERROR;

	private HttpStatus fallbackHttpStatus = DEFAULT_FALLBACK_HTTP_STATUS;

	private static final Map<String, HttpStatus> REASON_TO_STATUS_MAPPING = Map.ofEntries(
		new AbstractMap.SimpleImmutableEntry<>(ERROR_REASON_VALIDATION_FAIL, HttpStatus.FORBIDDEN),
		new AbstractMap.SimpleImmutableEntry<>(ERROR_REASON_NOT_FOUND, HttpStatus.NOT_FOUND),
		new AbstractMap.SimpleImmutableEntry<>(ERROR_REASON_INTERNAL, HttpStatus.INTERNAL_SERVER_ERROR)
	);

	public HttpStatus getFallbackHttpStatus() {
		return fallbackHttpStatus;
	}

	public void setFallbackHttpStatus(final HttpStatus status) {
		fallbackHttpStatus = status;
	}

	public HttpStatus convert(final String reason) {
		final HttpStatus status = REASON_TO_STATUS_MAPPING.get(reason);
		return status != null ? status : fallbackHttpStatus;
	}
}
