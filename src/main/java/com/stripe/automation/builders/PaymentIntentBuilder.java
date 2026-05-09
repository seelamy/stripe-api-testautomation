package com.stripe.automation.builders;

import java.util.HashMap;
import java.util.Map;

public class PaymentIntentBuilder {
    private final Map<String, Object> params;
    private PaymentIntentBuilder() {
        this.params = new HashMap<>();
    }
    public static PaymentIntentBuilder create() {
        return new PaymentIntentBuilder();
    }
    public PaymentIntentBuilder withAmount(long amount) {
        params.put("amount", amount);
        return this;
    }
    public PaymentIntentBuilder withCurrency(String currency) {
        params.put("currency", currency);
        return this;
    }
    public PaymentIntentBuilder withCustomer(String customerId) {
        params.put("customer", customerId);
        return this;
    }
    public PaymentIntentBuilder withPaymentMethod(String paymentMethodId) {
        params.put("payment_method", paymentMethodId);
        return this;
    }
    public PaymentIntentBuilder withConfirm(boolean confirm) {
        params.put("confirm", confirm);
        return this;
    }

    public PaymentIntentBuilder withAutomaticPaymentMethods(boolean enabled) {
        params.put("automatic_payment_methods[enabled]", enabled);
        params.put("automatic_payment_methods[allow_redirects]", "never");
        return this;
    }

    public PaymentIntentBuilder withCaptureMethod(String captureMethod) {
        params.put("capture_method", captureMethod);
        return this;
    }

    public PaymentIntentBuilder withDescription(String description) {
        params.put("description", description);
        return this;
    }

    public PaymentIntentBuilder withMetadata(String key, String value) {
        params.put("metadata[" + key + "]", value);
        return this;
    }

    public PaymentIntentBuilder withIdempotencyKey(String key) {
        params.put("idempotency_key", key);
        return this;
    }

    public Map<String, Object> build() {
        return params;
    }
}
