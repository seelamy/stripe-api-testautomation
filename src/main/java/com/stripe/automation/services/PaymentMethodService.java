package com.stripe.automation.services;

import com.stripe.automation.constants.Endpoints;
import io.restassured.response.Response;

import java.util.Map;

public class PaymentMethodService extends BaseService{
    public Response attach(String paymentMethodId, String customerId) {
        return post(Endpoints.PAYMENT_METHODS + "/" + paymentMethodId + "/attach",
                Map.of("customer", customerId));
    }

    public Response detach(String paymentMethodId) {
        return post(Endpoints.PAYMENT_METHODS + "/" + paymentMethodId + "/detach",
                Map.of());
    }

    public Response retrieve(String paymentMethodId) {
        return get(Endpoints.PAYMENT_METHODS + "/" + paymentMethodId);
    }

    public Response listByCustomer(String customerId) {
        return get(Endpoints.PAYMENT_METHODS,
                Map.of("customer", customerId, "type", "card"));
    }
}
