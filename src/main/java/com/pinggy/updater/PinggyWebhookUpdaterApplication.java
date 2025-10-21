package com.pinggy.updater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PinggyWebhookUpdaterApplication {

    public static void main(String[] args) {
        SpringApplication.run(PinggyWebhookUpdaterApplication.class, args);
    }

}
