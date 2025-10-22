package com.ebueno.infrastructure.jms;

import javax.jms.*;

/**
 * JMS Service that uses polling with connection per request.
 * This approach is better for low-frequency messaging scenarios.
 */
public class PollingBasedJmsService {
    protected final JmsConnectionManager connectionManager;
    protected final String queueName;

    public PollingBasedJmsService(String queueName) {
        this.connectionManager = JmsConnectionManager.getInstance();
        this.queueName = queueName;
    }

    /**
     * Receive a message from the queue using polling approach
     */
    public Message receiveMessage() throws JMSException {
        Connection connection = null;
        Session session = null;
        
        try {
            connection = connectionManager.getConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(queue);

            // Poll for message with timeout
            return consumer.receive(1000);
        } finally {
            if (session != null) {
                session.close();
            }
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
}