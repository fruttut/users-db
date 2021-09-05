package com.azoft.usersdb.db;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;

@Service
public class UserService {
	private final UserRepository repo;
	private final Logger logger;

	@Autowired
	public UserService(
		final UserRepository repo,
		final Logger logger
	) {
		this.repo = repo;
		this.logger = logger;
	}

	public UserEntity getUserById(final Integer id) throws UserNotFoundException {
		logger.debug("Looking up user by id {}", id);
		return repo.findById(id).orElseThrow(() -> new UserNotFoundException(id));
	}

	public UserEntity createUser(
		final String login,
		final String password,
		final String name,
		final String surname,
		final String email
	) {
		@Valid final UserEntity entity = new UserEntity(login, password, name, surname, email);
		logger.debug("Creating user {}", entity);
		return repo.save(entity);
	}
}
