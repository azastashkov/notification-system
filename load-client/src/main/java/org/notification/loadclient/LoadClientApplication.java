package org.notification.loadclient;

import org.notification.loadclient.service.LoadTestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class LoadClientApplication implements CommandLineRunner {

    private final LoadTestService loadTestService;

    public static void main(String[] args) {
        SpringApplication.run(LoadClientApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("Starting load test...");
        loadTestService.execute();
        log.info("Load test completed.");
        System.exit(0);
    }
}
