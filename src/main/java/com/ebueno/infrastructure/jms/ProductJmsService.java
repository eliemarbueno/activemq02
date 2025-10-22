package com.ebueno.infrastructure.jms;

import com.ebueno.domain.Product;
import com.ebueno.infrastructure.config.ConfigurationManager;

/**
 * JMS service implementation for Product entities.
 */
public class ProductJmsService extends BaseJmsService<Product> {
    private static ProductJmsService instance;

    private ProductJmsService() {
        super(
            ConfigurationManager.getInstance().getProperty("queue.product", "PRODUCT_QUEUE"),
            Product.class
        );
    }

    public static ProductJmsService getInstance() {
        if (instance == null) {
            instance = new ProductJmsService();
        }
        return instance;
    }
}