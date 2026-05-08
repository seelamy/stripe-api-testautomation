package com.stripe.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    private StripeError error;
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class StripeError {

        private String type;
        private String code;
        private String message;
        private String param;
        private String error;

        @JsonProperty("decline_code")
        private String declineCode;

        @JsonProperty("doc_url")
        private String docUrl;
    }
}
