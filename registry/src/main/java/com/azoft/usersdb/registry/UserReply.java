package com.azoft.usersdb.registry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UserReply {
	public final boolean success;
	public final UserError error;
	public final User data;

	@JsonCreator
	public UserReply(
		@JsonProperty(value = "success", required = true)
		final boolean success,
		@JsonProperty(value = "error", required = true)
		final UserError error,
		@JsonProperty(value = "data", required = true)
		final User data
	) {
		this.success = success;
		this.error = error;
		this.data = data;
	}

	@Override
	public String toString() {
		return String.format(
			"success: %b; error: %s; data: %s",
			success,
			error,
			data
		);
	}
}
