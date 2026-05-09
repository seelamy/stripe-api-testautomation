package com.stripe.automation.services;

import com.stripe.automation.constants.Endpoints;
import io.restassured.response.Response;

import java.util.Map;

public class RefundService extends BaseService{
    public Response create(Map<String, Object> params) {
        return post(Endpoints.REFUNDS, params);
    }

    public Response retrieve(String id) {
        return get(Endpoints.REFUNDS + "/" + id);
    }

    public Response list() {
        return get(Endpoints.REFUNDS);
    }

    public Response list(Map<String, Object> queryParams) {
        return get(Endpoints.REFUNDS, queryParams);
    }
    public Response createWithIdempotencyKey(Map<String, Object> params, String idempotencyKey) {
        return postWithIdempotencyKey(Endpoints.REFUNDS, params, idempotencyKey);
    }
}
