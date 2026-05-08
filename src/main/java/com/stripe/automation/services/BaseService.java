package com.stripe.automation.services;

import com.stripe.automation.config.ConfigManager;
import com.stripe.automation.spec.RequestSpec;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class BaseService {

    protected Response post(String endpoint, Map<String,Object> params){
        RequestSpecification spec=given().spec(RequestSpec.getBaseSpec());
        System.out.println("========== REQUEST ==========");
        System.out.println("POST " + ConfigManager.getBaseUrl() + endpoint);
        System.out.println("PARAMS: " + params);
        params.forEach((k,v)->spec.formParam(k, v));
        return spec.when().post(endpoint);
    }
    protected Response get(String endpoint) {
        return given()
                .spec(RequestSpec.getBaseSpec())
                .when()
                .get(endpoint);
    }

    protected Response get(String endpoint, Map<String, Object> queryParams) {
        RequestSpecification spec = given().spec(RequestSpec.getBaseSpec());
        queryParams.forEach((key, value) -> spec.queryParam(key, value));
        return spec.when().get(endpoint);
    }

    protected Response delete(String endpoint) {
        return given()
                .spec(RequestSpec.getBaseSpec())
                .when()
                .delete(endpoint);
    }

}
