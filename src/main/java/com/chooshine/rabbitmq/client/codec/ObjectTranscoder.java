package com.chooshine.rabbitmq.client.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectTranscoder<M extends Serializable> extends SerializeCodecFactory {

	@SuppressWarnings("unchecked")
	@Override
	public byte[] serialize(Object value) {
		if (value == null) {
			throw new NullPointerException("Can't serialize null");
		}
		byte[] result = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream os = null;
		try {
			bos = new ByteArrayOutputStream();
			os = new ObjectOutputStream(bos);
			M m = ((M) value);
			os.writeObject(m);
			os.close();
			bos.close();
			result = bos.toByteArray();
		} catch (IOException e) {
			throw new IllegalArgumentException("Non-serializable object", e);
		} finally {
			close(os);
			close(bos);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object deserizlize(byte[] in) {
		M result = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream is = null;
		try {
			if (in != null) {
				bis = new ByteArrayInputStream(in);
				is = new ObjectInputStream(bis);
				result = (M) is.readObject();
				is.close();
				bis.close();
			}
		} catch (IOException e) {
			logger.error(String.format(
					"Caught IOException decoding %d bytes of data",
					in == null ? 0 : in.length)
					+ e);
		} catch (ClassNotFoundException e) {
			logger.error(String.format("Caught CNFE decoding %d bytes of data",
					in == null ? 0 : in.length) + e);
		} finally {
			close(is);
			close(bis);
		}
		return result;

	}

	@Override
	protected void init() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void register(Class<?> class1) {
		// TODO Auto-generated method stub
		
	}

}
