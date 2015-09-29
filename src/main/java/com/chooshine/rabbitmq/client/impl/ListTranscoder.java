package com.chooshine.rabbitmq.client.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListTranscoder<M extends Serializable> extends SerializeCodecFactory{

	@SuppressWarnings("unchecked")
	@Override
	public byte[] serialize(Object value) {
		if (value == null)  
            throw new NullPointerException("Can't serialize null");  
          
        List<M> values = (List<M>) value;  
          
        byte[] results = null;  
        ByteArrayOutputStream bos = null;  
        ObjectOutputStream os = null;  
          
        try {  
            bos = new ByteArrayOutputStream();  
            os = new ObjectOutputStream(bos);  
            for (M m : values) {  
                os.writeObject(m);  
            }  
            os.close();  
            bos.close();  
            results = bos.toByteArray();  
        } catch (IOException e) {  
            throw new IllegalArgumentException("Non-serializable object", e);  
        } finally {  
            close(os);  
            close(bos);  
        }  
          
        return results; 
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object deserizlize(byte[] in) {
		List<M> list = new ArrayList<M>();
        ByteArrayInputStream bis = null;  
        ObjectInputStream is = null;  
        try {  
            if (in != null) {  
                bis = new ByteArrayInputStream(in);  
                is = new ObjectInputStream(bis);  
                while (bis.available() > 0) {  
                                      // while(true) will throw EOFException  
                    M m = (M)is.readObject();  
                    if (m == null) {  
                        break;  
                    }  
                      
                    list.add(m);  
                }  
                is.close();  
                bis.close();  
            }  
        } catch (IOException e) {    
           logger.error(String.format("Caught IOException decoding %d bytes of data",    
                    in == null ? 0 : in.length) + e);    
        } catch (ClassNotFoundException e) {    
        	logger.error(String.format("Caught CNFE decoding %d bytes of data",    
                    in == null ? 0 : in.length) + e);    
        }  finally {  
            close(is);  
            close(bis);  
        }  
        return  list;
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
