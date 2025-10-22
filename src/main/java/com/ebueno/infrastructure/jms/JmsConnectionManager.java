package com.ebueno.infrastructure.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import com.ebueno.infrastructure.config.ConfigurationManager;

/**
 * JMS Connection Manager that handles ActiveMQ connections.
 */
public class JmsConnectionManager {
    private static JmsConnectionManager instance;
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private final ConfigurationManager config;

    private JmsConnectionManager() {
        config = ConfigurationManager.getInstance();
        initializeConnectionFactory();
    }

    public static JmsConnectionManager getInstance() {
        if (instance == null) {
            instance = new JmsConnectionManager();
        }
        return instance;
    }

    private void initializeConnectionFactory() {
        String brokerUrl = config.getProperty("activemq.broker.url", "tcp://localhost:61616");
        String username = config.getProperty("activemq.username", "admin");
        String password = config.getProperty("activemq.password", "admin");

        connectionFactory = new ActiveMQConnectionFactory(username, password, brokerUrl);
    }

    /**
     * Get a JMS connection.
     * 
     * @return The JMS connection
     * @throws JMSException if there's an error creating the connection
     */
    public Connection getConnection() throws JMSException {
        if (connection == null) {
            connection = connectionFactory.createConnection();
            connection.start();
        }
        return connection;
    }

    /**
     * Close the JMS connection.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (JMSException e) {
                // Log error
                e.printStackTrace();
            }
        }
    }
}