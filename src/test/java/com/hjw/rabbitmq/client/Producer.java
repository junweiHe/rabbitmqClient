package com.hjw.rabbitmq.client;

import java.util.Random;

import com.chooshine.rabbitmq.client.EventTemplate;
import com.chooshine.rabbitmq.client.config.EventControlConfig;
import com.chooshine.rabbitmq.client.exception.SendRefuseException;
import com.chooshine.rabbitmq.client.impl.SimpleEventController;

public class Producer {
	
    private String defaultHost = "121.40.87.102";
	
    private String defaultExchange = "EXCHANGE_ALARM_QZJ";
	
    private String defaultQueue = "QUEUE_ALARM_QZJ";
	
	private SimpleEventController controller;
	
	private EventTemplate eventTemplate;
	
	public Producer(){
        EventControlConfig config = new EventControlConfig(defaultHost, 5672, "chooshine", "chooshine_2012", "alarm",
                5000, 0, 0);
		controller = SimpleEventController.getInstance(config);
		eventTemplate = controller.getEopEventTemplate();
		//controller.add(defaultQueue, defaultExchange, new ApiProcessEventProcessor());
		//controller.start();
		
	}
	public void sendMessage() throws SendRefuseException{
        int i = 0;
//		KryoCodecFactory codc = new KryoCodecFactory();
        Random ran = new Random();
        while (true) {
            int str = ran.nextInt(18);

            eventTemplate.send(defaultQueue, defaultExchange,
                    "{\"mp_items\":[{\"mp_id\":\"149\",\"data_time\":\"2015-11-03 15:30:00\",\"data_items\":[{\"name\":\"100003\",\"value\":\""
                            + str + "\"}]}]}");
            i++;
            //            try {
            //                TimeUnit.SECONDS.sleep(5);
            //            } catch (InterruptedException e) {
            //                e.printStackTrace();  
            //            }
            if (i == 5)
                break;
        }
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
