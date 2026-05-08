package com.stripe.automation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final Properties properties;

      static {
          String env = System.getProperty("env");
          if (env == null || env.isEmpty()) {
              env = "dev";
          }
          String configFile="config/"+env+".properties";
          logger.info("Loading config from: {}", configFile);
          properties = new Properties();
          try (InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream(configFile)) {
                if (inputStream != null) {
                    properties.load(inputStream);
                    logger.info("Config loaded successfully. Base URL: {}", properties.getProperty("base.url"));

                } else {
                    throw new RuntimeException("Configuration file not found: " + configFile);
                }
            } catch (Exception e) {
              logger.error("Failed to load config: {}", e.getMessage());
              throw new RuntimeException("Failed to load configuration: " + e.getMessage(), e);
          }
      }

      public static String getBaseUrl(){
          return properties.getProperty("base.url");
      }

      public static String getApiKey(){
          return properties.getProperty("api.key");
      }

      public static String getApiVersion(){
            return properties.getProperty("api.version");
      }
}
