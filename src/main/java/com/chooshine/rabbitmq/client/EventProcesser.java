package com.chooshine.rabbitmq.client;

public interface EventProcesser {
	public void process(Object obj);
}
