package com.chooshine.rabbitmq.client.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class MessageErrorHandler implements ErrorHandler {
	private static final Logger logger = LoggerFactory.getLogger(MessageErrorHandler.class);
	@Override
	public void handleError(Throwable arg0) {
		logger.error("",arg0);
	}
}
