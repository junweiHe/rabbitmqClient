package com.chooshine.rabbitmq.client.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

/**
 * Hassion方式实现的序列化与反序列化
 * @author Hejunwei
 *
 */
public class HessionCodecFactory implements CodecFactory {
	
	@Override
	public byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream baos = null;  
        HessianOutput output = null;  
        try {  
            baos = new ByteArrayOutputStream(1024);  
            output = new HessianOutput(baos);  
            output.startCall();  
            output.writeObject(obj);  
            output.completeCall();  
        } catch (final IOException ex) {  
            throw ex;  
        } finally {  
            if (output != null) {  
                try {  
                    baos.close();  
                } catch (final IOException ex) {  
                }  
            }  
        }  
        return baos != null ? baos.toByteArray() : null;  
	}
	
	@Override
	public Object deserizlize(byte[] content) throws IOException {
		Object obj = null;  
        ByteArrayInputStream bais = null;  
        HessianInput input = null;  
        try {  
            bais = new ByteArrayInputStream(content);  
            input = new HessianInput(bais);  
            input.startReply();  
            obj = input.readObject();  
            input.completeReply();  
        } catch (final IOException ex) {  
            throw ex;  
        } catch (final Throwable e) {  
        } finally {  
            if (input != null) {  
                try {  
                    bais.close();  
                } catch (final IOException ex) {  
                }  
            }  
        }  
        return obj;  
	}
}
