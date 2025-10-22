package com.ebueno.application;

import com.ebueno.domain.Category;
import com.ebueno.infrastructure.config.ConfigurationManager;
import com.ebueno.infrastructure.jms.ProductJmsService;
import com.ebueno.interfaces.controllers.CategoryController;
import com.ebueno.interfaces.rest.RestApi;

/**
 * Main application class that starts the message consumers and REST API.
 * This application demonstrates two different approaches for consuming messages:
 * 1. Products: Using MessageListener with persistent connection
 * 2. Categories: Using polling with connection per request
 */
public class Application {
    private static final ConfigurationManager config = ConfigurationManager.getInstance();
    private static final int CONSUMER_INTERVAL = config.getIntProperty("consumer.interval.ms", 60000); // Default 1 minute
    private static final int API_PORT = config.getIntProperty("api.port", 8080); // Default port 8080

    public static void main(String[] args) {
        // Start REST API
        try {
            RestApi api = new RestApi(API_PORT);
            api.start();    
        } catch (Exception e) {
            System.err.println("Error starting REST API: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Start Product consumer (using MessageListener - persistent connection)
        ProductJmsService.getInstance(); // This automatically starts the listener

        // Start Category consumer (using polling - connection per request)
        startCategoryConsumer();

        System.out.println("Application started with two different consumer approaches:");
        System.out.println("1. Products: Using MessageListener (persistent connection)");
        System.out.println("2. Categories: Using polling (connection per request)");
    }

    private static void startCategoryConsumer() {
        Thread categoryConsumer = new Thread(() -> {
            CategoryController categoryController = new CategoryController();
            System.out.println("Starting Category consumer (polling approach)...");
            
            while (true) {
                try {
                    Category category = categoryController.receiveCategory();
                    if (category != null) {
                        System.out.println("Received category (polling): " + category.getName());
                        // Process the category here
                    }
                    Thread.sleep(CONSUMER_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        categoryConsumer.setName("CategoryConsumer");
        categoryConsumer.start();
    }
}