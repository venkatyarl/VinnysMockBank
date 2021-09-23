package com.venkatyarlagadda.mockbank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class MockbankApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockbankApplication.class, args);
    }

}
