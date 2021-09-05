package com.azoft.usersdb.db;

import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = DBApplication.GET_USER_BY_ID_QUEUE_NAME, errorHandler = "errorHandler")
public final class GetUserByIdListener {
	private final UserService userService;
	private final Logger logger;

	@Autowired
	public GetUserByIdListener(
		final UserService userService,
		final Logger logger
	) {
		this.userService = userService;
		this.logger = logger;
	}

	@RabbitHandler
	public UserReply getUserById(final Integer id) throws UserNotFoundException {
		logger.debug("Queue {} received message {}", DBApplication.GET_USER_BY_ID_QUEUE_NAME, id);
		final UserEntity userEntity = userService.getUserById(id);
		logger.debug("Found user entity: {}", userEntity);
		final User user = new User(
			userEntity.id(),
			userEntity.login(),
			userEntity.password(),
			userEntity.name(),
			userEntity.surname(),
			userEntity.email(),
			userEntity.creationDate()
		);
		logger.debug("Converted user entity to user DTO: {}", user);
		return UserReply.successReply(user);
	}
}
