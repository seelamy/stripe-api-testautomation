package com.stripe.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Refund {
    private String id;
    private String object;
    private long amount;
    private String currency;
    private String status;
    private String reason;
    private Map<String, String> metadata;
    private long created;

    @JsonProperty("payment_intent")
    private String paymentIntent;

    @JsonProperty("charge")
    private String charge;
}
