/*
package io.nicheblog.dreamdiary;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

*/
/**
 * GatewaySession
 * 
 * @author nichefish
 *
 *//*

public class GatewaySession implements MqttCallback {
	private static final Log log = LogFactory.getLog(GatewaySession.class);
	
	private MqttClient mqtt = null;
	private MqttConnectOptions options;
	private MemoryPersistence persistence = new MemoryPersistence();
	private String serviceId;
	private String brokerUrl;
	private String clientId;
	private Map<String, GatewayTopicModel> topicTable = new HashMap<String, GatewayTopicModel>();
	private WebSocketSession socketSession;
	
	
	public GatewaySession(WebSocketSession socketSession, String serviceId, String brokerUrl, String clientId) {
		this.socketSession = socketSession;
		this.serviceId = serviceId;
		this.brokerUrl = brokerUrl;
		this.clientId = clientId;
		log.info("create GatewaySession : " + clientId);
	}
	
	public void connect() throws MqttException {
		log.info("connect GatewaySession : " + clientId);
		options = new MqttConnectOptions();
		options.setCleanSession(true);
		options.setKeepAliveInterval(30);
		mqtt = new MqttClient(brokerUrl, clientId, persistence);
		mqtt.setCallback(this);
		mqtt.connect(options);		
	}
	
	public void disconnect() throws MqttException {
		if(mqtt != null) {
			if( mqtt.isConnected()) {
				mqtt.disconnect();
			}
			mqtt.close();
		}
	}
	
    public void addTopic(String keys) {
    	List<String> actionArray = new ArrayList<String>();
    	String[] key = keys.split(",");

    	for(int i = 0; i < key.length; i++) {
    		String topicId = serviceId + "/" + key[i];
    		GatewayTopicModel gatewayTopic = topicTable.get(topicId);
    		if(gatewayTopic != null) {
    			gatewayTopic.addRef();
    			topicTable.put(topicId, gatewayTopic);
    			continue;
    		}
    		gatewayTopic = new GatewayTopicModel(topicId);
    		topicTable.put(topicId, gatewayTopic);
    		actionArray.add(topicId);
    	}

    	if(actionArray.size() > 0) {
    		String[] topicFilters = new String[actionArray.size()];
	    	for(int i = 0; i < topicFilters.length; i++) {
	    		topicFilters[i] = new String("/dashboard/" + actionArray.get(i));
	    		log.debug("subscribe -" + "/dashboard/" + actionArray.get(i)); 
	    	}
    		try {
				mqtt.subscribe(topicFilters);
			} catch (MqttException e) {
				log.warn(e);
				e.printStackTrace();
			}
    	}

    }	

    public void removeTopic(String keys) {
    	List<String> actionArray = new ArrayList<String>();
    	String[] key = keys.split(",");
    	
    	for(int i = 0; i < key.length; i++) {
    		String topicId = serviceId + "/" + key[i];
    		GatewayTopicModel gatewayTopic = topicTable.get(topicId);
    		if(gatewayTopic == null) continue;
    		
    		int refCount = gatewayTopic.removeRef();
    		if(refCount < 1) {
    			topicTable.remove(topicId);
    			actionArray.add(topicId);
    		}
    	}
    	
    	if(actionArray.size() > 0 ) {	
    		String[] topicFilters = new String[actionArray.size()];
	    	for(int i = 0; i < topicFilters.length; i++) {
	    		topicFilters[i] = new String("/dashboard/" + actionArray.get(i));
	    		log.debug("unsubscribe -" + "/dashboard/" + actionArray.get(i)); 
	    	}
    		try {
				mqtt.unsubscribe(topicFilters);
			} catch (MqttException e) {
				log.warn(e);
				e.printStackTrace();
			}
    	}
    }    
    
    public void publish(String key, String payload) {
    	String topic = "/dashboard/" + serviceId + "/" + key;
    	MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(2);
    	try {
			mqtt.publish(topic, message);
		} catch (MqttException e) {
			log.warn(e);
			e.printStackTrace();
		}
    }    
    
	@Override
	public void connectionLost(Throwable cause) {
		log.info("connectionLost GatewaySession : " + clientId);
		cause.printStackTrace();
		
		try {
			Thread.sleep(1000);
			mqtt.reconnect();
		} catch (MqttException | InterruptedException e) {
			log.warn(e);
			e.printStackTrace();
		}
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		if(socketSession != null && socketSession.isOpen()) {
			MessageModel message = new MessageModel();
			message.action = "4";
			message.data.topic = topic.replaceAll("/dashboard/" + serviceId + "/" , "");
			message.data.payload = new String(msg.toString());
			String text = mapper.writeValueAsString(message);
			socketSession.sendMessage(new TextMessage(text));
		} else {
			log.error("socketSession close !!!");
			disconnect();
		}
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		log.debug("deliveryComplete");
	}
}
*/
