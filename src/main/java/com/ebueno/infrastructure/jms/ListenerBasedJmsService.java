package com.ebueno.infrastructure.jms;

import javax.jms.*;

/**
 * JMS Service that uses MessageListener for persistent connection.
 * This approach is better for high-frequency messaging scenarios.
 */
public class ListenerBasedJmsService {
    protected final JmsConnectionManager connectionManager;
    protected final String queueName;
    protected Session session;
    protected MessageConsumer consumer;
    protected MessageListener messageListener;

    public ListenerBasedJmsService(String queueName, MessageListener listener) {
        this.connectionManager = JmsConnectionManager.getInstance();
        this.queueName = queueName;
        this.messageListener = listener;
        initializeConsumer();
    }

    private void initializeConsumer() {
        try {
            Connection connection = connectionManager.getConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            consumer = session.createConsumer(queue);
            consumer.setMessageListener(messageListener);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a message to the queue
     */
    public void sendMessage(String xmlContent) throws JMSException {
        Connection connection = null;
        Session session = null;
        
        try {
            connection = connectionManager.getConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(queue);

            TextMessage message = session.createTextMessage(xmlContent);
            producer.send(message);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Close the consumer resources
     */
    public void close() {
        try {
            if (consumer != null) {
                consumer.close();
            }
            if (session != null) {
                session.close();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}