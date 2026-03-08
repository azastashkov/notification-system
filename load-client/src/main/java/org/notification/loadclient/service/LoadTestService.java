package org.notification.loadclient.service;

import org.notification.loadclient.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class LoadTestService {

    private final RestTemplate restTemplate;
    private final DataSeeder dataSeeder;

    @Value("${notification-server.base-url:http://localhost:8080}")
    private String notificationServerUrl;

    @Value("${device-emulator.base-url:http://localhost:8083}")
    private String deviceEmulatorUrl;

    @Value("${load-test.users:100}")
    private int userCount;

    @Value("${load-test.notifications-per-user:10}")
    private int notificationsPerUser;

    @Value("${load-test.threads:10}")
    private int threadCount;

    private static final String[] TYPES = {"APNS", "FCM", "SMS", "EMAIL"};

    public LoadTestService(RestTemplate restTemplate, DataSeeder dataSeeder) {
        this.restTemplate = restTemplate;
        this.dataSeeder = dataSeeder;
    }

    public void execute() {
        log.info("=== Seeding test data ===");
        List<User> users = dataSeeder.seedUsers(userCount);
        dataSeeder.seedDevices(users);
        dataSeeder.seedTemplates();
        dataSeeder.seedUserSettings(users);

        log.info("Waiting for caches to warm...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        log.info("=== Starting load test: {} users x {} notifications = {} total ===",
                userCount, notificationsPerUser, userCount * notificationsPerUser);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        AtomicInteger progressCount = new AtomicInteger(0);
        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());

        List<Future<?>> futures = new ArrayList<>();
        Random random = new Random();

        for (User user : users) {
            for (int i = 0; i < notificationsPerUser; i++) {
                String type = TYPES[random.nextInt(TYPES.length)];
                futures.add(executor.submit(() -> {
                    long start = System.currentTimeMillis();
                    try {
                        Map<String, Object> request = new LinkedHashMap<>();
                        request.put("userId", user.getId());
                        request.put("type", type);
                        request.put("title", "Load Test Notification");
                        request.put("body", "This is a load test notification for " + user.getName());

                        restTemplate.postForObject(
                                notificationServerUrl + "/api/notifications",
                                request,
                                Map.class
                        );

                        successCount.incrementAndGet();
                        responseTimes.add(System.currentTimeMillis() - start);
                    } catch (Exception e) {
                        failureCount.incrementAndGet();
                        log.error("Failed to send notification: {}", e.getMessage());
                    }

                    int progress = progressCount.incrementAndGet();
                    if (progress % 100 == 0) {
                        log.info("Progress: {}/{} notifications sent", progress, userCount * notificationsPerUser);
                    }
                }));
            }
        }

        for (Future<?> future : futures) {
            try {
                future.get(60, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("Task failed: {}", e.getMessage());
            }
        }
        executor.shutdown();

        printSummary(successCount.get(), failureCount.get(), responseTimes);

        log.info("Waiting 10 seconds for notifications to be processed...");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        fetchDeviceStats();
    }

    private void printSummary(int success, int failure, List<Long> responseTimes) {
        int total = success + failure;
        double successRate = total > 0 ? (success * 100.0 / total) : 0;

        log.info("=== LOAD TEST SUMMARY ===");
        log.info("Total sent: {}", total);
        log.info("Success: {} ({}%)", success, String.format("%.1f", successRate));
        log.info("Failed: {}", failure);

        if (!responseTimes.isEmpty()) {
            Collections.sort(responseTimes);
            long avg = responseTimes.stream().mapToLong(Long::longValue).sum() / responseTimes.size();
            long p95 = responseTimes.get((int) (responseTimes.size() * 0.95));
            long p99 = responseTimes.get((int) (responseTimes.size() * 0.99));

            log.info("Avg response time: {}ms", avg);
            log.info("P95 response time: {}ms", p95);
            log.info("P99 response time: {}ms", p99);
        }
    }

    private void fetchDeviceStats() {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> stats = restTemplate.getForObject(
                    deviceEmulatorUrl + "/api/devices/stats", Map.class);
            log.info("=== DEVICE DELIVERY STATS ===");
            log.info("{}", stats);
        } catch (Exception e) {
            log.warn("Failed to fetch device stats: {}", e.getMessage());
        }
    }
}
