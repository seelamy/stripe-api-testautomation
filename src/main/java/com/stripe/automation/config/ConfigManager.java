package com.stripe.automation.config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static final Properties properties;

      static {
          String env = System.getProperty("env");
          if (env == null || env.isEmpty()) {
              env = "dev";
          }
          String configFile="config/"+env+".properties";
          properties = new Properties();
          try (InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream(configFile)) {
                if (inputStream != null) {
                    properties.load(inputStream);
                } else {
                    throw new RuntimeException("Configuration file not found: " + configFile);
                }
            } catch (Exception e) {
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
