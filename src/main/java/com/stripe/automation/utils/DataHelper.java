package com.stripe.automation.utils;

import java.util.UUID;

public class DataHelper {
    public static String randomEmail() {
        return "test_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    public static String randomName() {
        return "Test User " + UUID.randomUUID().toString().substring(0, 6);
    }
    public static String randomPhone() {
        return "+1" + (1000000000L + (long)(Math.random() * 9000000000L));
    }

    public static String randomString(int length) {
        return UUID.randomUUID().toString().replace("-", "").substring(0, Math.min(length, 32));
    }

    public static String randomDescription() {
        return "Automated test customer created at " + System.currentTimeMillis();
    }

    public static String idempotencyKey() {
        return UUID.randomUUID().toString();
    }
}
