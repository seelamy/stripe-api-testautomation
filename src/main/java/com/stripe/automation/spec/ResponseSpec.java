package com.stripe.automation.spec;

import com.stripe.automation.constants.StatusCodes;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;

public class ResponseSpec {
    public static ResponseSpecification success() {
        return new ResponseSpecBuilder()
                .expectStatusCode(StatusCodes.OK)
                .build();
    }

    public static ResponseSpecification customerSuccess() {
        return new ResponseSpecBuilder()
                .expectStatusCode(StatusCodes.OK)
                .expectBody("object", equalTo("customer"))
                .expectBody("id", startsWith("cus_"))
                .expectBody("livemode", equalTo(false))
                .build();
    }

    public static ResponseSpecification badRequest() {
        return new ResponseSpecBuilder()
                .expectStatusCode(StatusCodes.BAD_REQUEST)
                .expectBody("error.type", equalTo("invalid_request_error"))
                .build();
    }

    public static ResponseSpecification unauthorized() {
        return new ResponseSpecBuilder()
                .expectStatusCode(StatusCodes.UNAUTHORIZED)
                .build();
    }

    public static ResponseSpecification notFound() {
        return new ResponseSpecBuilder()
                .expectStatusCode(StatusCodes.NOT_FOUND)
                .expectBody("error.type", equalTo("invalid_request_error"))
                .build();
    }

    public static ResponseSpecification listSuccess() {
        return new ResponseSpecBuilder()
                .expectStatusCode(StatusCodes.OK)
                .expectBody("object", equalTo("list"))
                .build();
    }

}
