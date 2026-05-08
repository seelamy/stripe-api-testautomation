package com.stripe.automation.spec;

import com.stripe.automation.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

public class RequestSpec {
    public static RequestSpecification getBaseSpec(){
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getBaseUrl())
                .addHeader("Authorization","Bearer "+ConfigManager.getApiKey())
                .addHeader("Stripe-Version",ConfigManager.getApiVersion())
                .setContentType("application/x-www-form-urlencoded")
                .addFilter(new AllureRestAssured())
                .build();

    }
}
