package com.cmcc.rabbitmq.client;

public interface EventProcesser {
	public void process(Object obj);
}
