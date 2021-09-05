package com.azoft.usersdb.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

import java.util.AbstractMap;
import java.util.Map;

@SpringBootApplication
public class DBApplication {
	public static final String GET_USER_BY_ID_QUEUE_NAME = "user.get-by-id";
	public static final String CREATE_USER_QUEUE_NAME = "user.create";

	private static final Map<String, Class<?>> dtoIdClassMapping = Map.ofEntries(
		new AbstractMap.SimpleImmutableEntry<>("userId", Integer.class),
		new AbstractMap.SimpleImmutableEntry<>("createUserRequest", CreateUserRequest.class),
		new AbstractMap.SimpleImmutableEntry<>("userReply", UserReply.class)
	);

	@Bean
	@Qualifier(GET_USER_BY_ID_QUEUE_NAME)
	public Queue getUserByIdQueue() {
		return new Queue(GET_USER_BY_ID_QUEUE_NAME, false, false, false, null);
	}

	@Bean
	@Qualifier(CREATE_USER_QUEUE_NAME)
	public Queue createUserQueue() {
		return new Queue(CREATE_USER_QUEUE_NAME, false, false, false, null);
	}

	@Bean
	public MessageConverter messageConverter() {
		final DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
		typeMapper.setIdClassMapping(dtoIdClassMapping);
		final Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
		converter.setJavaTypeMapper(typeMapper);
		return converter;
	}

	@Bean
	public RabbitListenerErrorHandler errorHandler() {
		return new ConvertingExceptionToReplyErrorHandler(new StackTraceStringifier());
	}

	@Bean
	@Scope("prototype")
	public Logger logger(final InjectionPoint injectionPoint) {
		final Class<?> wiredClass = injectionPoint.getMember().getDeclaringClass();
		return LoggerFactory.getLogger(wiredClass);
	}

	public static void main(final String... args) {
		SpringApplication.run(DBApplication.class, args);
	}
}
