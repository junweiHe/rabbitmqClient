package com.hjw.rabbitmq.client;

import java.util.concurrent.TimeUnit;

import com.cmcc.rabbitmq.client.EventTemplate;
import com.cmcc.rabbitmq.client.config.EventControlConfig;
import com.cmcc.rabbitmq.client.exception.SendRefuseException;
import com.cmcc.rabbitmq.client.impl.KryoCodecFactory;
import com.cmcc.rabbitmq.client.impl.SimpleEventController;

public class Producer {
	
	private String defaultHost = "192.168.12.85";
	
	private String defaultExchange = "EXCHANGE_APP_ROUTE";
	
	private String defaultQueue = "APP_ROUTE_QUEUE";
	
	private SimpleEventController controller;
	
	private EventTemplate eventTemplate;
	
	public Producer(){
		EventControlConfig config = new EventControlConfig(defaultHost,5672,"approute","approute");
		controller = SimpleEventController.getInstance(config);
		eventTemplate = controller.getEopEventTemplate();
		//controller.add(defaultQueue, defaultExchange, new ApiProcessEventProcessor());
		//controller.start();
		
	}
	public void sendMessage() throws SendRefuseException{
		int i = 1;
//		KryoCodecFactory codc = new KryoCodecFactory();
//		while(true){
			eventTemplate.send(defaultQueue, defaultExchange, "{\"raw\":-1449872}");
//			i++;
//			try {
//				TimeUnit.SECONDS.sleep(5);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			if(i==10) break;
//		}
	}
	public static void main(String[] args) {
		Producer p = new Producer();
		try {
			p.sendMessage();
		} catch (SendRefuseException e) {
			e.printStackTrace();
		}
	}
}
