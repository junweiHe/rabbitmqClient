package com.chooshine.rabbitmq.client;

import com.chooshine.rabbitmq.client.codec.CodecFactory;
import com.chooshine.rabbitmq.client.exception.SendRefuseException;

public interface EventTemplate {
	void send(String queueName,String exchangeName,Object eventContent) throws SendRefuseException;  
    
    void send(String queueName,String exchangeName,Object eventContent,CodecFactory codecFactory) throws SendRefuseException; 
}
