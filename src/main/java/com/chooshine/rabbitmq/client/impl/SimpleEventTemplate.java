package com.chooshine.rabbitmq.client.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.util.StringUtils;

import com.chooshine.rabbitmq.client.CodecFactory;
import com.chooshine.rabbitmq.client.EventTemplate;
import com.chooshine.rabbitmq.client.bean.EventMessage;
import com.chooshine.rabbitmq.client.exception.SendRefuseException;

public class SimpleEventTemplate implements EventTemplate {
	private static final Logger logger = LoggerFactory.getLogger(SimpleEventTemplate.class);
	private AmqpTemplate template;
	
	private CodecFactory codec;
	
	private SimpleEventController eec;
//  
	public SimpleEventTemplate(AmqpTemplate template,
          CodecFactory codec, SimpleEventController eec) {
		this.template = template;
		this.codec = codec;
		this.eec = eec;
	}  
//	public SimpleEventTemplate(AmqpTemplate template,CodecFactory codec) {
//		this.template = template;
//		this.codec = codec;
//	}
//	
//	public SimpleEventTemplate(AmqpTemplate template) {
//		this.template = template;
//		this.codec = new HessionCodecFactory();
//	}
	
	@Override
	public void send(String queueName, String exchangeName, Object eventContent)
			throws SendRefuseException {
		send(queueName, exchangeName, eventContent, codec);
	}

	@Override
	public void send(String queueName, String exchangeName,
			Object eventContent, CodecFactory codecFactory)
			throws SendRefuseException {
		if (StringUtils.isEmpty(queueName) || StringUtils.isEmpty(exchangeName)) {  
            throw new SendRefuseException("queueName exchangeName can not be empty.");  
        }  
          
      if (!eec.beBinded(exchangeName, queueName))  
          eec.declareBinding(exchangeName, queueName);  
  
        byte[] eventContentBytes = null;  
        if (codecFactory == null) {  
            if (eventContent == null) {  
                logger.warn("Find eventContent is null,are you sure...");  
            } else {  
                throw new SendRefuseException(  
                        "codecFactory must not be null ,unless eventContent is null.");  
            }  
        } else {  
            try {  
                eventContentBytes = codecFactory.serialize(eventContent);  
            } catch (IOException e) {  
                throw new SendRefuseException("codecFactory serialize fail.",e);  
            }  
        }  
  
        // 构造成Message  
        EventMessage msg = new EventMessage(queueName, exchangeName,
                eventContentBytes);  
        try {  
            template.convertAndSend(exchangeName, queueName, msg);
        } catch (AmqpException e) {  
            logger.error("send event fail. Event Message : [" + eventContent + "]", e);  
            throw new SendRefuseException("send event fail");  
        }  
	}

}
