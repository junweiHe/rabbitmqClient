package com.cmcc.rabbitmq.client.impl;

import java.io.Closeable;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmcc.rabbitmq.client.CodecFactory;

public abstract class SerializeCodecFactory implements CodecFactory{
	protected static final Logger logger = LoggerFactory.getLogger(SerializeCodecFactory.class);
	void close(Closeable close){
		if(close!=null){
			try {
				close.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected abstract void init() throws Exception;
	
	protected abstract void register (Class<?> class1);
}
