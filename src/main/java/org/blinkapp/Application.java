package org.blinkapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = FlywayAutoConfiguration.class)
@RestController
public class Application {

    @RequestMapping("/")
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}