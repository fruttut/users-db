package com.azoft.usersdb.db;

public final class UserReply {
	public final boolean success;
	public final UserError error;
	public final User data;

	public static UserReply successReply(final User data) {
		return new UserReply(true, null, data);
	}

	public static UserReply errorReply(final UserError error) {
		return new UserReply(false, error, null);
	}

	private UserReply(final boolean success, final UserError error, final User data) {
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
