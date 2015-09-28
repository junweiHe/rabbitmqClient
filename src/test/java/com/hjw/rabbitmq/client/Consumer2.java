package com.hjw.rabbitmq.client;

import com.cmcc.rabbitmq.client.EventProcesser;
import com.cmcc.rabbitmq.client.config.EventControlConfig;
import com.cmcc.rabbitmq.client.impl.SimpleEventController;

public class Consumer2 implements EventProcesser{
private String defaultHost = "192.168.13.34";
	
	private String defaultExchange = "EXCHANGE_DIRECT_TEST";
	
	private String defaultQueue = "QUEUE_TEST";
	
	private SimpleEventController controller;
	public Consumer2() {
		EventControlConfig config = new EventControlConfig(defaultHost);
		controller = SimpleEventController.getInstance(config);
		controller.add(defaultQueue, defaultExchange, this);
		//controller.add(defaultQueue, defaultExchange, new Consumre2());
		controller.start();
	}
	@Override
	public void process(Object obj) {
		System.out.println("consumer2:"+obj);
	}
	public static void main(String[] args) {
		Consumer2 c = new Consumer2();
		while(true){
		}
	}
}
