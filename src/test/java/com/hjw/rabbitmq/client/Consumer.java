package com.hjw.rabbitmq.client;


import com.cmcc.rabbitmq.client.EventProcesser;
import com.cmcc.rabbitmq.client.config.EventControlConfig;
import com.cmcc.rabbitmq.client.impl.KryoCodecFactory;
import com.cmcc.rabbitmq.client.impl.SimpleEventController;

public class Consumer implements EventProcesser{
	
	private String defaultHost = "192.168.12.89";
	
	private String defaultExchange = "EXCHANGE_APP_ROUTE";
	
	private String defaultQueue = "APP_ROUTE_QUEUE";
	
	private SimpleEventController controller;
	public Consumer() {
		EventControlConfig config = new EventControlConfig(defaultHost,5672,"approute","approute");
		controller = SimpleEventController.getInstance(config);
		KryoCodecFactory codc = new KryoCodecFactory();
//		codc.register(HashMap.class);
		controller.add(defaultQueue, defaultExchange, this).start();
		//controller.add(defaultQueue, defaultExchange, new Consumre2());
//		controller.start();
	}
	@Override
	public void process(Object obj) {
//		if(obj instanceof HashMap){
//			HashMap<String,Object> map = (HashMap<String, Object>) obj;
//			System.out.println(map);
//		}else{
			System.out.println(obj);
//		}
	}
	public static void main(String[] args) {
		Consumer c = new Consumer();
		while(true){
		}
	}
//	static class Consumre1 implements EventProcesser{
//		@Override
//		public void process(Object obj) {
//			System.out.println("consumer1:"+obj);
//		}
//		
//	}
//	static class Consumre2 implements EventProcesser{
//		@Override
//		public void process(Object obj) {
//			System.out.println("consumer2:"+obj);
//		}
//		
//	}
}
