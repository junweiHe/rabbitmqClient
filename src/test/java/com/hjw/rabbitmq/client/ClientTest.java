package com.hjw.rabbitmq.client;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.chooshine.rabbitmq.client.EventProcesser;
import com.chooshine.rabbitmq.client.EventTemplate;
import com.chooshine.rabbitmq.client.config.EventControlConfig;
import com.chooshine.rabbitmq.client.exception.SendRefuseException;
import com.chooshine.rabbitmq.client.impl.SimpleEventController;

public class ClientTest {
	
	private String defaultHost = "121.";
	
	private String defaultExchange = "EXCHANGE_DIRECT_TEST";
	
	private String defaultQueue = "QUEUE_TEST";
	
	private SimpleEventController controller;
	
	private EventTemplate eventTemplate;
	
//	@Before
	public void init() throws IOException{
		EventControlConfig config = new EventControlConfig(defaultHost);
		controller = SimpleEventController.getInstance(config);
		eventTemplate = controller.getEopEventTemplate();
		controller.add(defaultQueue, defaultExchange, new ApiProcessEventProcessor());
		controller.start();
	}
	
//	@Test
	public void sendString() throws SendRefuseException {
		eventTemplate.send(defaultQueue, defaultExchange, "hello world");
	}
	
//	@After
	public void end() throws InterruptedException{
		Thread.sleep(2000);
		try {
			controller.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	class ApiProcessEventProcessor implements EventProcesser{
		@Override
		public void process(Object e) {//消费程序这里只是打印信息
			Assert.assertNotNull(e);
			System.out.println(e);
//			if(e instanceof People){
//				People people = (People)e;
//				System.out.println(people.getSpouse());
//				System.out.println(people.getFriends());
//			}
		}
	}
}
