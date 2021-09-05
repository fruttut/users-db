package com.azoft.usersdb.registry;

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

	public CreateUserRequest(
		final String login,
		final String password,
		final String name,
		final String surname,
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
