package com.azoft.usersdb.db;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

@Entity(name = "users")
public final class UserEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@NotBlank
	private String login;
	@NotBlank
	private String password;
	@NotBlank
	private String name;
	@NotBlank
	private String surname;
	@NotBlank
	private String email;
	@CreationTimestamp
	private Timestamp creationDate;

	protected UserEntity() {
	}

	public UserEntity(
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

	public Integer id() {
		return id;
	}

	public String login() {
		return login;
	}

	public String password() {
		return password;
	}

	public String name() {
		return name;
	}

	public String surname() {
		return surname;
	}

	public String email() {
		return email;
	}

	public Timestamp creationDate() {
		return creationDate;
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
