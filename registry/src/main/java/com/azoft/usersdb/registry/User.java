package com.azoft.usersdb.registry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

public final class User {
	public final int id;
	public final String login;
	public final String password;
	public final String name;
	public final String surname;
	public final String email;
	public final Timestamp creationDate;

	@JsonCreator
	public User(
		@JsonProperty(value = "id", required = true)
		final int id,
		@JsonProperty(value = "login", required = true)
		final String login,
		@JsonProperty(value = "password", required = true)
		final String password,
		@JsonProperty(value = "name", required = true)
		final String name,
		@JsonProperty(value = "surname", required = true)
		final String surname,
		@JsonProperty(value = "email", required = true)
		final String email,
		@JsonProperty(value = "creationDate", required = true)
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
