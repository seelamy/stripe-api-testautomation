package com.stripe.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Map;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Customer {
    private String id;
    private String object;
    private String email;
    private String name;
    private String phone;
    private String description;
    private Address address;
    private Map<String, String> metadata;
    private boolean livemode;
    private long created;
    private Boolean deleted;

    @JsonProperty("default_source")
    private String defaultSource;

    @JsonProperty("invoice_settings")
    private Map<String, Object> invoiceSettings;
}
