package com.starlingbank.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigManager {
    private static final String CONFIG_FILE_NAME = "config.properties"; // Adjusted path
    private static final String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";

    private static final Logger LOGGER = Logger.getLogger(ConfigManager.class.getName());

    private final Properties properties;

    public ConfigManager() {
        this.properties = new Properties();
        loadProperties();
    }

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

    public String getAccessToken() {
        return properties.getProperty(ACCESS_TOKEN_KEY);
    }
}
