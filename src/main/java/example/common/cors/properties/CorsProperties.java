package example.common.cors.properties;

import example.common.cors.properties.types.LogLevel;
import example.common.cors.properties.allowed.CorsAllowedProperties;
import lombok.Builder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Builder
@ConfigurationProperties(prefix = "app.cors")
@ConfigurationPropertiesBinding
public record CorsProperties(
        @NestedConfigurationProperty
        CorsAllowedProperties allowed,
        String[] exposedHeaders,
        Boolean allowsCredentials,
        Long maxAge,
        LogLevel logLevel
) {
    public CorsProperties {
        if (allowed == null) {
            allowed = CorsAllowedProperties.defaultInstance();
        }

        if (exposedHeaders == null || exposedHeaders.length == 0) {
            exposedHeaders = new String[] {"*"};
        }

        if (allowsCredentials == null) {
            allowsCredentials = true;
        }

        if (maxAge == null) {
            maxAge = 1800L;
        }

        if (logLevel == null) {
            logLevel = LogLevel.INFO;
        }
    }
}