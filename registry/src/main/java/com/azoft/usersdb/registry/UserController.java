package com.azoft.usersdb.registry;

import org.slf4j.Logger;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
public final class UserController {
	private final RabbitTemplate rabbit;
	private final Queue getUserByIdQueue;
	private final Queue createUserQueue;
	private final ErrorReasonToHttpStatusConverter reasonToStatusConverter;
	private final Logger logger;
	private final ParameterizedTypeReference<UserReply> userTypeReference;

	@Autowired
	public UserController(
		final RabbitTemplate rabbit,
		@Qualifier(RegistryApplication.GET_USER_BY_ID_QUEUE_NAME) final Queue getUserByIdQueue,
		@Qualifier(RegistryApplication.CREATE_USER_QUEUE_NAME) final Queue createUserQueue,
		final ErrorReasonToHttpStatusConverter reasonToStatusConverter,
		final Logger logger
	) {
		this.rabbit = rabbit;
		this.getUserByIdQueue = getUserByIdQueue;
		this.createUserQueue = createUserQueue;
		this.reasonToStatusConverter = reasonToStatusConverter;
		this.logger = logger;
		this.userTypeReference = new ParameterizedTypeReference<>() {
		};
	}

	@GetMapping("/user/{id}")
	public User getUserById(@PathVariable("id") final int id) {
		logger.info("GET /user/{}", id);
		final String queueName = getUserByIdQueue.getName();
		logger.debug("Sending request to queue {}", queueName);
		final UserReply reply = rabbit.convertSendAndReceiveAsType(
			queueName,
			id,
			userTypeReference
		);
		logger.debug("Received reply from queue {}: {}", queueName, reply);
		return processReply(reply);
	}

	@PostMapping("/user")
	@ResponseBody
	public User createUser(@Valid @RequestBody final CreateUserRequest request) {
		logger.info("POST /user, request body: {}", request);
		final String queueName = createUserQueue.getName();
		logger.debug("Sending request to queue {}", queueName);
		final UserReply reply = rabbit.convertSendAndReceiveAsType(
			queueName,
			request,
			userTypeReference
		);
		logger.debug("Received reply from queue {}: {}", queueName, reply);
		return processReply(reply);
	}

	private User processReply(final UserReply reply) {
		if (reply == null) {
			logger.debug("Reply is null, will return {}", HttpStatus.REQUEST_TIMEOUT);
			throw new ResponseStatusException(HttpStatus.REQUEST_TIMEOUT);
		}
		if (!reply.success) {
			final HttpStatus errorStatus = reasonToStatusConverter.convert(reply.error.reason);
			logger.debug("Error reply, will return {}", errorStatus);
			throw new ResponseStatusException(errorStatus, reply.error.message);
		}
		logger.debug("Reply is ok");
		return reply.data;
	}
}
