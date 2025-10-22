package com.ebueno.infrastructure.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration manager class that handles loading and accessing application configuration.
 */
public class ConfigurationManager {
    private static Properties properties;
    private static ConfigurationManager instance;

    private ConfigurationManager() {
        properties = new Properties();
        try {
            // First try to load from classpath
            properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (Exception e) {
            try {
                // Fallback to external config file
                properties.load(new FileInputStream("config/application.properties"));
            } catch (IOException ex) {
                throw new RuntimeException("Failed to load configuration file from both classpath and external location", ex);
            }
        }
    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Get a configuration property value.
     * 
     * @param key The property key
     * @return The property value
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Get a configuration property value with a default value.
     * 
     * @param key The property key
     * @param defaultValue The default value if the property is not found
     * @return The property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Get a configuration property value as an integer.
     * 
     * @param key The property key
     * @param defaultValue The default value if the property is not found
     * @return The property value as an integer
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = properties.getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
}