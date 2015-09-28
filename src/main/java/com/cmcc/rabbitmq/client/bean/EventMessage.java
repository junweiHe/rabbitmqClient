package com.cmcc.rabbitmq.client.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 消息传输持有对象
 * @author Hejunwei
 *
 */
@SuppressWarnings("serial")
public class EventMessage implements Serializable {
	private String exchangeName;
	private String queueName;
	
	private byte[] eventContent;
	
	public EventMessage(){}
	public EventMessage(String queueName,String exchangeName,byte[] eventContent){
		this.queueName = queueName;
		this.exchangeName = exchangeName;
		this.eventContent = eventContent;
	}
	public String getExchangeName() {
		return exchangeName;
	}
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}
	public String getQueueName() {
		return queueName;
	}
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
	public byte[] getEventContent() {
		return eventContent;
	}
	public void setEventContent(byte[] eventContent) {
		this.eventContent = eventContent;
	}
	@Override
	public String toString() {
		return "EopEventMessage [queueName=" + queueName + ", exchangeName="  
                + exchangeName + ", eventContent=" + Arrays.toString(eventContent)
                + "]";
	}
	
}
