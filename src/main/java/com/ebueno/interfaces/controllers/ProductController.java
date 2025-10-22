package com.ebueno.interfaces.controllers;

import com.ebueno.domain.Product;
import com.ebueno.infrastructure.jms.ProductJmsService;
import javax.jms.JMSException;
import javax.xml.bind.JAXBException;

/**
 * Controller class for handling Product related operations.
 */
public class ProductController {
    private final ProductJmsService productJmsService;

    public ProductController() {
        this.productJmsService = ProductJmsService.getInstance();
    }

    /**
     * Send a product to the queue.
     * 
     * @param product The product to send
     * @throws JMSException if there's an error with JMS operations
     * @throws JAXBException if there's an error with XML marshalling
     */
    public void sendProduct(Product product) throws JMSException, JAXBException {
        productJmsService.sendMessage(product);
    }

    /**
     * Receive a product from the queue.
     * 
     * @return The received product
     * @throws JMSException if there's an error with JMS operations
     * @throws JAXBException if there's an error with XML unmarshalling
     */
    public Product receiveProduct() throws JMSException, JAXBException {
        return productJmsService.receiveMessage();
    }
}