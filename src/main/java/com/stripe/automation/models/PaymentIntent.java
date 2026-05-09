package com.stripe.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentIntent {
    private String id;
    private String object;
    private long amount;
    private String currency;
    private String status;
    private String customer;
    private String description;
    private Map<String, String> metadata;
    private boolean livemode;
    private long created;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("amount_received")
    private long amountReceived;

    @JsonProperty("capture_method")
    private String captureMethod;

    @JsonProperty("latest_charge")
    private String latestCharge;

    @JsonProperty("cancellation_reason")
    private String cancellationReason;

    @JsonProperty("client_secret")
    private String clientSecret;

}
