package com.pinggy.updater.service;

import com.pinggy.updater.props.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class PinggyService {

    private final AppProperties properties;
    private Process pinggyProcess;

    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void updateWebhook() {
        try {
            if (pinggyProcess != null && pinggyProcess.isAlive()) {
                pinggyProcess.destroy();
                log.info("Предыдущий Pinggy процесс остановлен.");
                Thread.sleep(3000);
            }
//            ProcessBuilder pb = new ProcessBuilder(
//                    "ssh", "-R 80:" + properties.getHost() + ":" + properties.getPort(),
//                    "serveo.net"
//            );
            ProcessBuilder pb = new ProcessBuilder(
                    "ssh",
                    "-p", "443",
                    "-o", "StrictHostKeyChecking=no",
                    "-o", "BatchMode=yes",
                    "-R0:" + properties.getHost() + ":" + properties.getPort(),
                    "qr@free.pinggy.io"
            );
            pb.redirectErrorStream(true);
            pinggyProcess = pb.start();

            log.info("Pinggy процесс запущен.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(pinggyProcess.getInputStream()));
            String line;
            String url = null;

            long timeout = System.currentTimeMillis() + 20_000;
            int count = 0;
            while ((line = reader.readLine()) != null && System.currentTimeMillis() < timeout) {
                log.info(line);
                log.info("count: {}", count);
                if (line.contains("https://")) {
                    if(count == 0) {
                        count++;
                    } else {
                        url = line.trim();
                        break;
                    }
//                    log.info("URL: {}", line.substring("Forwarding HTTP traffic from https".length()));
//                    url = line.substring("Forwarding HTTP traffic from https".length()).trim();
//                    break;
                }
            }

            if (url == null) {
                log.error("Не удалось получить URL Pinggy.");
                return;
            }

            String fullWebhookUrl = url + properties.getEndpoint();
            log.info("Устанавливаю webhook: {}", fullWebhookUrl);

            String telegramUrl = String.format(
                    "https://api.telegram.org/bot%s/setWebhook?url=%s",
                    properties.getBotToken(),
                    fullWebhookUrl
            );
            log.info("Telegram URL: {}", telegramUrl);

            HttpURLConnection connection = (HttpURLConnection) new URL(telegramUrl).openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            String responseMessage = connection.getResponseMessage();
            log.info("Ответ Telegram: {}, {}", responseCode, responseMessage);

        } catch (Exception e) {
            log.error("Ошибка при обновлении webhook", e);
        }
    }
}
