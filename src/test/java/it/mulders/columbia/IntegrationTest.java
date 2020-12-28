package it.mulders.columbia;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ActiveProfiles("test")
@SpringJUnitConfig
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@WebAppConfiguration
public @interface IntegrationTest {
}
