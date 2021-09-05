package com.azoft.usersdb.db;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
@RabbitListener(queues = DBApplication.CREATE_USER_QUEUE_NAME, errorHandler = "errorHandler")
public final class CreateUserListener {
	private final UserService userService;
	private final Logger logger;

	@Autowired
	public CreateUserListener(
		final UserService userService,
		final Logger logger
	) {
		this.userService = userService;
		this.logger = logger;
	}

	@RabbitHandler
	public UserReply createUser(@Valid final CreateUserRequest request) {
		logger.debug("Queue {} received message {}", DBApplication.CREATE_USER_QUEUE_NAME, request);
		final UserEntity userEntity = userService.createUser(
			request.login,
			request.password,
			request.name,
			request.surname,
			request.email
		);
		logger.debug("Created user entity: {}", userEntity);
		final User createdUser = new User(
			userEntity.id(),
			userEntity.login(),
			userEntity.password(),
			userEntity.name(),
			userEntity.surname(),
			userEntity.email(),
			userEntity.creationDate()
		);
		logger.debug("Converted user entity to user DTO: {}", createdUser);
		return UserReply.successReply(createdUser);
	}
}
