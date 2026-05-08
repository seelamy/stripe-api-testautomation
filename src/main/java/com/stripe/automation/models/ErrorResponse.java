package com.stripe.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class ErrorResponse {
    private StripeError error;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StripeError {

        private String type;
        private String code;
        private String message;
        private String param;

        @JsonProperty("decline_code")
        private String declineCode;

        @JsonProperty("doc_url")
        private String docUrl;
    }
}
