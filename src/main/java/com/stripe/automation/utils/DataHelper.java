package com.stripe.automation.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

public class DataHelper {

    // Stripe ID patterns
    private static final Pattern CUSTOMER_ID = Pattern.compile("^cus_[a-zA-Z0-9]{14,}$");
    private static final Pattern PAYMENT_INTENT_ID = Pattern.compile("^pi_[a-zA-Z0-9]{14,}$");
    private static final Pattern CHARGE_ID = Pattern.compile("^ch_[a-zA-Z0-9]{14,}$");
    private static final Pattern REFUND_ID = Pattern.compile("^re_[a-zA-Z0-9]{14,}$");
    private static final Pattern SUBSCRIPTION_ID = Pattern.compile("^sub_[a-zA-Z0-9]{14,}$");
    private static final Pattern INVOICE_ID = Pattern.compile("^in_[a-zA-Z0-9]{14,}$");
    private static final Pattern PRODUCT_ID = Pattern.compile("^prod_[a-zA-Z0-9]{14,}$");
    private static final Pattern PRICE_ID = Pattern.compile("^price_[a-zA-Z0-9]{14,}$");

    // ID Validators
    public static boolean isValidCustomerId(String id) {
        return id != null && CUSTOMER_ID.matcher(id).matches();
    }

    public static boolean isValidPaymentIntentId(String id) {
        return id != null && PAYMENT_INTENT_ID.matcher(id).matches();
    }

    public static boolean isValidChargeId(String id) {
        return id != null && CHARGE_ID.matcher(id).matches();
    }

    public static boolean isValidRefundId(String id) {
        return id != null && REFUND_ID.matcher(id).matches();
    }

    public static boolean isValidSubscriptionId(String id) {
        return id != null && SUBSCRIPTION_ID.matcher(id).matches();
    }

    public static boolean isValidInvoiceId(String id) {
        return id != null && INVOICE_ID.matcher(id).matches();
    }

    public static boolean isValidProductId(String id) {
        return id != null && PRODUCT_ID.matcher(id).matches();
    }

    public static boolean isValidPriceId(String id) {
        return id != null && PRICE_ID.matcher(id).matches();
    }

    // Random data generators
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

    public static Map<String, String> randomMetadata(int count) {
        Map<String, String> metadata = new HashMap<>();
        for (int i = 1; i <= count; i++) {
            metadata.put("key_" + String.format("%02d", i), randomString(20));
        }
        return metadata;
    }

}