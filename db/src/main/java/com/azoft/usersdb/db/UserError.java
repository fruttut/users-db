package com.azoft.usersdb.db;

public final class UserError {
	public final String reason;
	public final String message;

	public UserError(final String reason, final String message) {
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
