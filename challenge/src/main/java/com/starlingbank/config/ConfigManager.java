package com.starlingbank.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
    private Properties properties;

    public ConfigManager() {
        this.properties = new Properties();
        loadProperties("challenge/resources/config.properties");
    }

    private void loadProperties(String filePath) {
        try (FileInputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAccessToken() {
        return properties.getProperty("ACCESS_TOKEN");
    }
}