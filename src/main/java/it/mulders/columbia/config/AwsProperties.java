package it.mulders.columbia.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

@Configuration
@ConfigurationProperties("columbia.aws")
@Data
public class AwsProperties {
    private String accessKeyId;
    private String region = Region.AWS_GLOBAL.toString();
    private String secretAccessKey;

    public Region getRegion() {
        return Region.of(this.region);
    }
}
