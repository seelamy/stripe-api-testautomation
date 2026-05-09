package com.stripe.automation.services;

import com.stripe.automation.constants.Endpoints;
import io.restassured.response.Response;

import java.util.Map;

public class PaymentIntentService extends BaseService {
    public Response create(Map<String, Object> params) {
        return post(Endpoints.PAYMENT_INTENTS, params);
    }

    public Response retrieve(String id) {
        return get(Endpoints.PAYMENT_INTENTS + "/" + id);
    }
    public Response confirm(String id, Map<String, Object> params) {
        return post(Endpoints.PAYMENT_INTENTS + "/" + id + "/confirm", params);
    }

    public Response capture(String id) {
        return post(Endpoints.PAYMENT_INTENTS + "/" + id + "/capture", Map.of());
    }

    public Response capture(String id, Map<String, Object> params) {
        return post(Endpoints.PAYMENT_INTENTS + "/" + id + "/capture", params);
    }

    public Response cancel(String id) {
        return post(Endpoints.PAYMENT_INTENTS + "/" + id + "/cancel", Map.of());
    }

    public Response list() {
        return get(Endpoints.PAYMENT_INTENTS);
    }
    public Response list(Map<String, Object> queryParams) {
        return get(Endpoints.PAYMENT_INTENTS, queryParams);
    }
    public Response createWithIdempotencyKey(Map<String, Object> params, String idempotencyKey) {
        return postWithIdempotencyKey(Endpoints.PAYMENT_INTENTS, params, idempotencyKey);
    }

}
