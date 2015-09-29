package com.chooshine.rabbitmq.client.exception;

@SuppressWarnings("serial")
public class SendRefuseException extends Exception{
	public SendRefuseException(String message){
		super(message);
	}
	public SendRefuseException(Exception e){
		super(e);
	}
	public SendRefuseException(String message,Exception e){
		super(message,e);
	}
}
