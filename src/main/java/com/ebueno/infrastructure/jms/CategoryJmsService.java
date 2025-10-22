package com.ebueno.infrastructure.jms;

import com.ebueno.domain.Category;
import com.ebueno.infrastructure.config.ConfigurationManager;

/**
 * JMS service implementation for Category entities.
 */
public class CategoryJmsService extends BaseJmsService<Category> {
    private static CategoryJmsService instance;

    private CategoryJmsService() {
        super(
            ConfigurationManager.getInstance().getProperty("queue.category", "CATEGORY_QUEUE"),
            Category.class
        );
    }

    public static CategoryJmsService getInstance() {
        if (instance == null) {
            instance = new CategoryJmsService();
        }
        return instance;
    }
}