package com.starlingbank.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is responsible for managing the configuration of the application.
 * It loads the properties from a configuration file and provides a method to access them.
 */
public class ConfigManager {
    // The name of the configuration file
    private static final String CONFIG_FILE_NAME = "config.properties";
    // The key used to retrieve the access token from the properties
    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";

    // Logger for this class
    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());

    // Properties object to hold the loaded properties
    private final Properties properties;

    /**
     * Constructor for the ConfigManager class.
     * It initializes the properties object and loads the properties from the configuration file.
     */
    public ConfigManager() {
        this.properties = new Properties();
        loadProperties();
    }

    /**
     * This method loads the properties from the configuration file into the properties object.
     * If the file is not found, it throws an IOException.
     */
    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME)) {
            if (input == null) {
                throw new IOException("Property file '" + CONFIG_FILE_NAME + "' not found in the classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot load properties from " + CONFIG_FILE_NAME, e);
        }
    }

    /**
     * This method retrieves the access token from the properties.
     * @return The access token as a string.
     */
    public String getAccessToken() {
        return properties.getProperty(ACCESS_TOKEN_KEY);
    }
}
