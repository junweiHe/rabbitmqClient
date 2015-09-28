package com.cmcc.rabbitmq.client;

import java.util.Map;

public interface EventController {
	/** 
     * 控制器启动方法 
     */  
    void start();  
      
    /** 
     * 获取发送模版 
     */  
    EventTemplate getEopEventTemplate();  
      
    /** 
     * 绑定消费程序到对应的exchange和queue 
     */  
    EventController add(String queueName, String exchangeName, EventProcesser eventProcesser);  
      
    /**
     * Map 中 key为 queueName value 为exchageName
     */  
    EventController add(Map<String,String> bindings, EventProcesser eventProcesser);
}
