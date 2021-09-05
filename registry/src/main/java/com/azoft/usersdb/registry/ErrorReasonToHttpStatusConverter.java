package com.azoft.usersdb.registry;

import org.springframework.http.HttpStatus;

public interface ErrorReasonToHttpStatusConverter {
	HttpStatus convert(final String reason);
}
