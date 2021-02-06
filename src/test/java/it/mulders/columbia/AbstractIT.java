package it.mulders.columbia;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ActiveProfiles("test")
@Slf4j
@SpringBootTest(
        // classes = { SpringConfiguration.class, TestConfigurationFactory.class},
        properties = { "environment = test" },
        webEnvironment = RANDOM_PORT
)
@SpringJUnitConfig
@Testcontainers
public abstract class AbstractIT {
    @Container
    protected static final PostgreSQLContainer DATABASE = new PostgreSQLContainer("postgres:9.3-alpine");//prepareContainer();

    @DynamicPropertySource
    static void configureDatasource(DynamicPropertyRegistry registry) {
        log.info("Started PostgreSQL test container @ {}", DATABASE.getJdbcUrl());
        registry.add("spring.datasource.url", DATABASE::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE::getUsername);
        registry.add("spring.datasource.password", DATABASE::getPassword);
    }

}
