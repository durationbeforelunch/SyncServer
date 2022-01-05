package com.sync.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:server.properties")
public class Starter {
    public static void main(String[] args) {
         new SpringApplicationBuilder(Starter.class)
                .headless(false).run(args);
    }
}
// App Starter Class