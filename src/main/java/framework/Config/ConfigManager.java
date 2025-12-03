package framework.config;

import org.aeonbits.owner.ConfigFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigManager {
    
     private static volatile ConfigManager instance;
    private final TestConfig config;
    
    private ConfigManager() {
        try {
            this.config = ConfigFactory.create(TestConfig.class);
            log.info("Configuration loaded successfully for environment: {}", config.testEnvironment());
            validateConfiguration();
        } catch (Exception e) {
            log.error("Failed to load configuration", e);
            throw new RuntimeException("Configuration initialization failed", e);
        }
    }
    
    private void validateConfiguration() {
        if (config.baseUrl() == null || config.baseUrl().isEmpty()) {
            throw new IllegalStateException("Base URL must be specified in the configuration");
        }
        if (config.requestTimeout() <= 0) {
            throw new IllegalStateException("Request timeout must be a positive integer");
        }
        if (config.connectionTimeout() <= 0) {
            throw new IllegalStateException("Connection timeout must be a positive integer");
        }
        log.info("Configuration validated successfully");
    }

    public TestConfig getConfig() {
        return config;
    }
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }
}
