package com.hjw.rabbitmq.client;


import com.chooshine.rabbitmq.client.EventProcesser;
import com.chooshine.rabbitmq.client.config.EventControlConfig;
import com.chooshine.rabbitmq.client.impl.SimpleEventController;

public class Consumer implements EventProcesser{
	
    private String defaultHost = "121.40.87.102";
	
    private String defaultExchange = "EXCHANGE_ALARM_QZJ";

    private String defaultQueue = "QUEUE_ALARM_HUXUN";
	
	private SimpleEventController controller;
	public Consumer() {
        EventControlConfig config = new EventControlConfig(defaultHost, 5672, "chooshine", "chooshine_2012", "alarm");
		controller = SimpleEventController.getInstance(config);
        //		KryoCodecFactory codc = new KryoCodecFactory();
//		codc.register(HashMap.class);
        controller.add(defaultQueue, defaultExchange, this).start();
		//controller.add(defaultQueue, defaultExchange, new Consumre2());
//		controller.start();
	}
	@Override
	public void process(Object obj) {
			System.out.println(obj);
	}
	public static void main(String[] args) {
		Consumer c = new Consumer();
	}
}
