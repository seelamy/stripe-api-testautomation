package com.stripe.automation.services;

import com.stripe.automation.constants.Endpoints;
import io.restassured.response.Response;

import java.util.Map;

public class CustomerService extends BaseService {
    public Response create(Map<String, Object> params) {
        return post(Endpoints.CUSTOMERS, params);
    }

    public Response retrieve(String id) {
        return get(Endpoints.CUSTOMERS + "/" + id);
    }

    public Response update(String id, Map<String, Object> params) {
        return post(Endpoints.CUSTOMERS + "/" + id, params);
    }

    public Response deleted(String id) {
        return delete(Endpoints.CUSTOMERS + "/" + id);
    }

    public Response list() {
        return get(Endpoints.CUSTOMERS);
    }

    public Response list(Map<String, Object> queryParams) {
        return get(Endpoints.CUSTOMERS, queryParams);
    }
}
