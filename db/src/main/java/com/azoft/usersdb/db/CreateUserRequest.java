package com.azoft.usersdb.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public final class CreateUserRequest {
	@NotBlank
	public final String login;
	@NotBlank
	public final String password;
	@NotBlank
	public final String name;
	@NotBlank
	public final String surname;
	@NotBlank
	public final String email;

	@JsonCreator
	public CreateUserRequest(
		@JsonProperty(value = "login", required = true)
		final String login,
		@JsonProperty(value = "password", required = true)
		final String password,
		@JsonProperty(value = "name", required = true)
		final String name,
		@JsonProperty(value = "surname", required = true)
		final String surname,
		@JsonProperty(value = "email", required = true)
		final String email
	) {
		this.login = login;
		this.password = password;
		this.name = name;
		this.surname = surname;
		this.email = email;
	}

	@Override
	public String toString() {
		return String.format(
			"login: %s; password: %s; name: %s; surname: %s; email: %s",
			login,
			password,
			name,
			surname,
			email
		);
	}
}
