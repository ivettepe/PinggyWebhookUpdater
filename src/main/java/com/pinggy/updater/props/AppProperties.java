package com.pinggy.updater.props;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class AppProperties {
    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.port}")
    private int port;

    @Value("${bot.endpoint}")
    private String endpoint;

    @Value("${bot.host}")
    private String host;
}
