package com.stripe.automation.builders;

import java.util.HashMap;
import java.util.Map;

public class RefundBuilder {
    private final Map<String, Object> params;

    private RefundBuilder() {
        this.params = new HashMap<>();
    }

    public static RefundBuilder create() {
        return new RefundBuilder();
    }

    public RefundBuilder withPaymentIntent(String paymentIntentId) {
        params.put("payment_intent", paymentIntentId);
        return this;
    }
    public RefundBuilder withAmount(long amount) {
        params.put("amount", amount);
        return this;
    }

    public RefundBuilder withReason(String reason) {
        params.put("reason", reason);
        return this;
    }

    public RefundBuilder withMetadata(String key, String value) {
        params.put("metadata[" + key + "]", value);
        return this;
    }

    public Map<String, Object> build() {
        return params;
    }
}
