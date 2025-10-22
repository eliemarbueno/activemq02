package com.ebueno.interfaces.controllers;

import com.ebueno.domain.Category;
import com.ebueno.infrastructure.jms.CategoryJmsService;
import javax.jms.JMSException;
import javax.xml.bind.JAXBException;

/**
 * Controller class for handling Category related operations.
 */
public class CategoryController {
    private final CategoryJmsService categoryJmsService;

    public CategoryController() {
        this.categoryJmsService = CategoryJmsService.getInstance();
    }

    /**
     * Send a category to the queue.
     * 
     * @param category The category to send
     * @throws JMSException if there's an error with JMS operations
     * @throws JAXBException if there's an error with XML marshalling
     */
    public void sendCategory(Category category) throws JMSException, JAXBException {
        categoryJmsService.sendMessage(category);
    }

    /**
     * Receive a category from the queue.
     * 
     * @return The received category
     * @throws JMSException if there's an error with JMS operations
     * @throws JAXBException if there's an error with XML unmarshalling
     */
    public Category receiveCategory() throws JMSException, JAXBException {
        return categoryJmsService.receiveMessage();
    }
}