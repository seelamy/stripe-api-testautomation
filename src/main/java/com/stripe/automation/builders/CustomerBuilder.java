package com.stripe.automation.builders;

import java.util.HashMap;
import java.util.Map;

public class CustomerBuilder {
    private final Map<String, Object> params;
    private CustomerBuilder() {
        this.params = new HashMap<>();
    }

    public static CustomerBuilder create() {
        return new CustomerBuilder();
    }
    // Basic fields
    public CustomerBuilder withEmail(String email) {
        params.put("email", email);
        return this;
    }

    public CustomerBuilder withName(String name) {
        params.put("name", name);
        return this;
    }

    public CustomerBuilder withPhone(String phone) {
        params.put("phone", phone);
        return this;
    }

    public CustomerBuilder withDescription(String description) {
        params.put("description", description);
        return this;
    }
    // Address fields
    public CustomerBuilder withAddressLine1(String line1) {
        params.put("address[line1]", line1);
        return this;
    }

    public CustomerBuilder withAddressLine2(String line2) {
        params.put("address[line2]", line2);
        return this;
    }

    public CustomerBuilder withCity(String city) {
        params.put("address[city]", city);
        return this;
    }

    public CustomerBuilder withState(String state) {
        params.put("address[state]", state);
        return this;
    }

    public CustomerBuilder withPostalCode(String postalCode) {
        params.put("address[postal_code]", postalCode);
        return this;
    }

    public CustomerBuilder withCountry(String country) {
        params.put("address[country]", country);
        return this;
    }
    // Metadata
    public CustomerBuilder withMetadata(String key, String value) {
        params.put("metadata[" + key + "]", value);
        return this;
    }

    // Payment
    public CustomerBuilder withPaymentMethod(String paymentMethodId) {
        params.put("payment_method", paymentMethodId);
        return this;
    }

    // Build
    public Map<String, Object> build() {
        return params;
    }

    public CustomerBuilder withMetadataMap(Map<String, String> metadata) {
        metadata.forEach((key, value) -> params.put("metadata[" + key + "]", value));
        return this;
    }
}
