package com.ebueno.infrastructure.jms;

import javax.jms.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Abstract base class for JMS operations.
 * @param <T> The type of entity being handled
 */
public abstract class BaseJmsService<T> {
    protected final JmsConnectionManager connectionManager;
    protected final String queueName;
    protected final Class<T> entityClass;
    protected Session consumerSession;
    protected MessageConsumer consumer;

    protected BaseJmsService(String queueName, Class<T> entityClass) {
        this.connectionManager = JmsConnectionManager.getInstance();
        this.queueName = queueName;
        this.entityClass = entityClass;
        initializeConsumer();
    }

    private void initializeConsumer() {
        try {
            Connection connection = connectionManager.getConnection();
            consumerSession = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = consumerSession.createQueue(queueName);
            consumer = consumerSession.createConsumer(queue);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a message to the queue.
     * 
     * @param entity The entity to send
     * @throws JMSException if there's an error with JMS operations
     * @throws JAXBException if there's an error with XML marshalling
     */
    public void sendMessage(T entity) throws JMSException, JAXBException {
        Connection connection = null;
        Session session = null;
        
        try {
            connection = connectionManager.getConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(queue);

            String xmlContent = convertToXml(entity);
            TextMessage message = session.createTextMessage(xmlContent);
            producer.send(message);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Receive a message from the queue.
     * 
     * @return The received entity
     * @throws JMSException if there's an error with JMS operations
     * @throws JAXBException if there's an error with XML unmarshalling
     */
    public T receiveMessage() throws JMSException, JAXBException {
        Message message = consumer.receive(1000); // 1 second timeout
        if (message instanceof TextMessage) {
            String xmlContent = ((TextMessage) message).getText();
            return convertFromXml(xmlContent);
        }
        return null;
    }

    protected String convertToXml(T entity) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(entityClass);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter writer = new StringWriter();
        marshaller.marshal(entity, writer);
        return writer.toString();
    }

    protected T convertFromXml(String xml) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(entityClass);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return entityClass.cast(unmarshaller.unmarshal(new StringReader(xml)));
    }
}