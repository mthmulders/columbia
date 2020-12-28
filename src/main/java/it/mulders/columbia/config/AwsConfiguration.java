package it.mulders.columbia.config;

import it.mulders.columbia.vaults.VaultService;
import it.mulders.columbia.vaults.VaultServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.services.glacier.GlacierClient;

@Configuration
public class AwsConfiguration {
    @Bean
    public AwsCredentialsProvider awsCredentialsProvider(final AwsProperties awsProperties) {
        return () -> AwsBasicCredentials.create(awsProperties.getAccessKeyId(), awsProperties.getSecretAccessKey());
    }

    @Bean
    public GlacierClient glacierClient(final AwsProperties awsProperties, final AwsCredentialsProvider credentialsProvider) {
        return GlacierClient.builder()
                .credentialsProvider(credentialsProvider)
                .region(awsProperties.getRegion())
                .build();
    }

    @Bean
    VaultService vaultService(final GlacierClient glacierClient) {
        return new VaultServiceImpl(glacierClient);
    }
}
