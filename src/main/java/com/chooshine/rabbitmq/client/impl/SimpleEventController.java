package com.chooshine.rabbitmq.client.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.util.StringUtils;

import com.chooshine.rabbitmq.client.EventController;
import com.chooshine.rabbitmq.client.EventProcesser;
import com.chooshine.rabbitmq.client.EventTemplate;
import com.chooshine.rabbitmq.client.codec.CodecFactory;
import com.chooshine.rabbitmq.client.codec.HessionCodecFactory;
import com.chooshine.rabbitmq.client.config.EventControlConfig;
import com.chooshine.rabbitmq.client.handler.MessageAdapterHandler;
import com.chooshine.rabbitmq.client.handler.MessageErrorHandler;
/** 
 * 和rabbitmq通信的控制器，主要负责： 
 * <p>1、和rabbitmq建立连接</p> 
 * <p>2、声明exChange和queue以及它们的绑定关系</p> 
 * <p>3、启动消息监听容器，并将不同消息的处理者绑定到对应的exchange和queue上</p> 
 * <p>4、持有消息发送模版以及所有exchange、queue和绑定关系的本地缓存</p> 
 * @author Hejunwei
 * 
 */ 
public class SimpleEventController implements EventController{
	public static final Logger logger = LoggerFactory.getLogger(SimpleEventController.class);
	private CachingConnectionFactory rabbitConnectionFactory;  
    
    private EventControlConfig config;  
      
    private RabbitAdmin rabbitAdmin;  
      
    private CodecFactory defaultCodecFactory = new HessionCodecFactory();  
      
    private SimpleMessageListenerContainer msgListenerContainer;   
      
    private MessageAdapterHandler msgAdapterHandler = new MessageAdapterHandler();  
      
    private MessageConverter serializerMessageConverter = new SerializerMessageConverter(); // 直接指定  
    //queue cache, key = exchangeName  
    private Map<String, DirectExchange> exchanges = new HashMap<String,DirectExchange>();  
    //queue cache, key = queueName  
    private Map<String, Queue> queues = new HashMap<String, Queue>();  
    //bind relation of queue to exchange cache, value is exchangeName | queueName
    private Set<String> binded = new HashSet<String>();  
      
    private EventTemplate eventTemplate;   
      
    private AtomicBoolean isStarted = new AtomicBoolean(false);
      
    private static SimpleEventController defaultEventController;
      
    public synchronized static SimpleEventController getInstance(EventControlConfig config){
        if(defaultEventController==null){  
            defaultEventController = new SimpleEventController(config);  
        }  
        return defaultEventController;  
    }

    public static  SimpleEventController createInstance(EventControlConfig config) {
        return new SimpleEventController(config);
    }

    /**
     * spring 配置使用
     * @param config+
     */
    private SimpleEventController(EventControlConfig config){  
        if (config == null) {  
            throw new IllegalArgumentException("Config can not be null.");  
        }  
        this.config = config;  
        initRabbitConnectionFactory();  
        // 初始化AmqpAdmin  
        rabbitAdmin = new RabbitAdmin(rabbitConnectionFactory);  
        // 初始化RabbitTemplate  
        RabbitTemplate rabbitTemplate = new RabbitTemplate(rabbitConnectionFactory);  
        rabbitTemplate.setMessageConverter(serializerMessageConverter);  
        eventTemplate = new SimpleEventTemplate(rabbitTemplate,defaultCodecFactory, this);  
    }  
      
    /** 
     * 初始化rabbitmq连接 
     */  
    private void initRabbitConnectionFactory() {
        rabbitConnectionFactory = new CachingConnectionFactory();
        if(config.getAddress() != null && config.getAddress().length() > 0) {
            rabbitConnectionFactory.setAddresses(config.getAddress());
        } else {
            rabbitConnectionFactory.setHost(config.getServerHost());
            rabbitConnectionFactory.setPort(config.getPort());
        }
        rabbitConnectionFactory.setChannelCacheSize(config.getEventMsgProcessNum());
        rabbitConnectionFactory.setUsername(config.getUsername());
        rabbitConnectionFactory.setPassword(config.getPassword());
        if (!StringUtils.isEmpty(config.getVirtualHost())) {
            rabbitConnectionFactory.setVirtualHost(config.getVirtualHost());  
        }
    }  
      
    /** 
     * 注销程序 
     */  
    public synchronized void destroy() throws Exception {  
        if (!isStarted.get()) {  
            return;  
        }  
        msgListenerContainer.stop();  
        eventTemplate = null;  
        rabbitAdmin = null;  
        rabbitConnectionFactory.destroy();  
    }  
      
    @Override  
    public void start() {  
        if (isStarted.get()) {  
            return;  
        }  
        Set<String> mapping = msgAdapterHandler.getAllBinding();  
        for (String relation : mapping) {  
            String[] relaArr = relation.split("\\|");
            declareBinding(relaArr[1], relaArr[0]);
        }  
        initMsgListenerAdapter();  
        isStarted.set(true);  
    }  
      
    /** 
     * 初始化消息监听器容器 
     */  
    private void initMsgListenerAdapter(){  
        MessageListener listener = new MessageListenerAdapter(msgAdapterHandler,serializerMessageConverter);  
        msgListenerContainer = new SimpleMessageListenerContainer();  
        msgListenerContainer.setConnectionFactory(rabbitConnectionFactory);  
        msgListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);  
        msgListenerContainer.setMessageListener(listener);  
        msgListenerContainer.setErrorHandler(new MessageErrorHandler());  
        msgListenerContainer.setPrefetchCount(config.getPrefetchSize()); // 设置每个消费者消息的预取值  
        msgListenerContainer.setConcurrentConsumers(config.getEventMsgProcessNum());  
        msgListenerContainer.setTxSize(config.getPrefetchSize());//设置有事务时处理的消息数  
        msgListenerContainer.setQueues(queues.values().toArray(new Queue[queues.size()]));  
        msgListenerContainer.start();  
    }  
  
    @Override  
    public EventTemplate getEopEventTemplate() {  
        return eventTemplate;  
    }  
  
    @Override  
    public EventController add(String queueName, String exchangeName,EventProcesser eventProcesser) {  
        return add(queueName, exchangeName, eventProcesser, defaultCodecFactory);  
    }  
      
    public EventController add(String queueName, String exchangeName,EventProcesser eventProcesser,CodecFactory codecFactory) {  
        msgAdapterHandler.add(queueName, exchangeName, eventProcesser, codecFactory);  
        if(isStarted.get()){  
            initMsgListenerAdapter();
        }  
        return this;  
    }  
  
    @Override  
    public EventController add(Map<String, String> bindings,  
            EventProcesser eventProcesser) {  
        return add(bindings, eventProcesser,defaultCodecFactory);  
    }  
  
    public EventController add(Map<String, String> bindings,  
            EventProcesser eventProcesser, CodecFactory codecFactory) {  
        for(Map.Entry<String, String> item: bindings.entrySet())   
            msgAdapterHandler.add(item.getKey(),item.getValue(), eventProcesser,codecFactory);  
        return this;  
    }  
      
    /** 
     * exchange和queue是否已经绑定 
     */  
    protected boolean beBinded(String exchangeName, String queueName) {  
        return binded.contains(exchangeName+"|"+queueName);  
    }  
      
    /** 
     * 声明exchange和queue已经它们的绑定关系 
     */  
    protected synchronized void declareBinding(String exchangeName, String queueName) {  
        String bindRelation = exchangeName+"|"+queueName;  
        if (binded.contains(bindRelation)) return;  
          
        boolean needBinding = false;  
        DirectExchange directExchange = exchanges.get(exchangeName);  
        if(directExchange == null) {  
            directExchange = new DirectExchange(exchangeName, true, false, null);  
            exchanges.put(exchangeName, directExchange);  
            rabbitAdmin.declareExchange(directExchange);//声明exchange  
            needBinding = true;  
        }  
          
        Queue queue = queues.get(queueName);  
        if(queue == null) {  
            queue = new Queue(queueName, true, false, false);  
            queues.put(queueName, queue); 
            rabbitAdmin.declareQueue(queue);    //声明queue  
            needBinding = true; 
        }  
        
          
        if(needBinding) {  
            Binding binding = BindingBuilder.bind(queue).to(directExchange).with(queueName);//将queue绑定到exchange  
            rabbitAdmin.declareBinding(binding);//声明绑定关系  
            binded.add(bindRelation);  
        }  
    } 
    
}
