package com.stripe.automation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListResponse<T> {
    private String object;
    private List<T> data;

    @JsonProperty("has_more")
    private boolean hasMore;

    private String url;

}
