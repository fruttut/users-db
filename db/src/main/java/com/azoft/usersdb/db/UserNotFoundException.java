package com.azoft.usersdb.db;

public final class UserNotFoundException extends Exception {
	public UserNotFoundException(final int id) {
		super("no user with id " + id);
	}
}
