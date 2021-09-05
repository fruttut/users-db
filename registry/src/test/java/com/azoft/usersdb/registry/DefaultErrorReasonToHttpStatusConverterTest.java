package com.azoft.usersdb.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class DefaultErrorReasonToHttpStatusConverterTest {
	private DefaultErrorReasonToHttpStatusConverter converter;

	@BeforeEach
	public void setup() {
		converter = new DefaultErrorReasonToHttpStatusConverter();
	}

	@Test
	public void shouldReturnForbiddenHttpStatus_whenErrorReasonIsValidationFailure() {
		assertEquals(
			HttpStatus.FORBIDDEN,
			converter.convert(DefaultErrorReasonToHttpStatusConverter.ERROR_REASON_VALIDATION_FAIL),
			"should return FORBIDDEN HTTP status when error reason is validation failure"
		);
	}

	@Test
	public void shouldReturnNotFoundHttpStatus_whenErrorReasonIsNotFoundFailure() {
		assertEquals(
			HttpStatus.NOT_FOUND,
			converter.convert(DefaultErrorReasonToHttpStatusConverter.ERROR_REASON_NOT_FOUND),
			"should return NOT_FOUND HTTP status when error reason is 'not found' failure"
		);
	}

	@Test
	public void shouldReturnInternalServerErrorHttpStatus_whenErrorReasonIsInternalFailure() {
		assertEquals(
			HttpStatus.INTERNAL_SERVER_ERROR,
			converter.convert(DefaultErrorReasonToHttpStatusConverter.ERROR_REASON_INTERNAL),
			"should return INTERNAL_SERVER_ERROR HTTP status when error reason is internal failure"
		);
	}

	@Test
	public void shouldReturnFallbackHttpStatus_whenAnyOtherErrorReason() {
		final String mockErrorReason = "UNKNOWN_ERROR_REASON";
		assertEquals(
			DefaultErrorReasonToHttpStatusConverter.FALLBACK_HTTP_STATUS,
			converter.convert(mockErrorReason),
			"should return fallback HTTP status for any other error reason"
		);
	}
}
