package com.azoft.usersdb.db;

import java.sql.Timestamp;

public final class User {
	public final int id;
	public final String login;
	public final String password;
	public final String name;
	public final String surname;
	public final String email;
	public final Timestamp creationDate;

	public User(
		final int id,
		final String login,
		final String password,
		final String name,
		final String surname,
		final String email,
		final Timestamp creationDate
	) {
		this.id = id;
		this.login = login;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.creationDate = creationDate;
	}

	@Override
	public String toString() {
		return String.format(
			"id: %d; login: %s; password: %s; name: %s; surname: %s; email: %s; creationDate: %s",
			id,
			login,
			password,
			name,
			surname,
			email,
			creationDate
		);
	}
}
