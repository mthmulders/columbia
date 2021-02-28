package it.mulders.columbia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Columbia {
    public static void main( final String... args ) {
        SpringApplication.run(Columbia.class, args);
    }
}
