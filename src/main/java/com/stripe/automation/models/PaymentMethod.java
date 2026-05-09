package com.stripe.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentMethod {

    private String id;
    private String object;
    private String type;
    private String customer;
    private Card card;
    private long created;
    private boolean livemode;

    @JsonProperty("billing_details")
    private BillingDetails billingDetails;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Card {
        private String brand;
        private String last4;
        private String country;
        private String funding;

        @JsonProperty("exp_month")
        private int expMonth;

        @JsonProperty("exp_year")
        private int expYear;
    }

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BillingDetails {
        private String name;
        private String email;
        private String phone;
        private Address address;
    }
}