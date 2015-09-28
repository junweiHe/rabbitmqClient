package com.cmcc.rabbitmq.client;

import com.cmcc.rabbitmq.client.exception.SendRefuseException;

public interface EventTemplate {
	void send(String queueName,String exchangeName,Object eventContent) throws SendRefuseException;  
    
    void send(String queueName,String exchangeName,Object eventContent,CodecFactory codecFactory) throws SendRefuseException; 
}
