package com.azoft.usersdb.registry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UserError {
	public final String reason;
	public final String message;

	@JsonCreator
	public UserError(
		@JsonProperty("reason")
		final String reason,
		@JsonProperty("message")
		final String message
	) {
		this.reason = reason;
		this.message = message;
	}

	@Override
	public String toString() {
		return String.format(
			"reason: %s; message: %s",
			reason,
			message
		);
	}
}
