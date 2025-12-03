package framework.config;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

@LoadPolicy(LoadType.MERGE)
@Sources({
    "system:properties",
    "system:env",
    "file:src/test/config/${test.environment}.properties",
    "file:src/test/config/default.properties"
})
public interface TestConfig extends Config {
    
    @Key("base.url")
    String baseUrl();

    @Key("test.environment")
    @DefaultValue("prod")
    String testEnvironment();
    
    @Key("request.timeout")
    int requestTimeout();
    
    @Key("connection.timeout")
    int connectionTimeout();
}

