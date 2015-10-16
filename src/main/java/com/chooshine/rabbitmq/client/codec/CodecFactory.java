package com.chooshine.rabbitmq.client.codec;

import java.io.IOException;

/**
 * 序列化反序列化接口
 * @author Hejunwei
 *
 */
public interface CodecFactory {
	public byte[] serialize(Object obj) throws IOException;
	
	public Object deserizlize(byte[] content) throws IOException;
}
