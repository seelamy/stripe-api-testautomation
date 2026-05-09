package com.stripe.automation.services;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.CodeLanguage;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.stripe.automation.config.ConfigManager;
import com.stripe.automation.spec.RequestSpec;
import com.stripe.automation.utils.ExtentReportManager;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class BaseService {
    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);
    private void logToReport(String method, String endpoint, Map<String, Object> params, Response response) {
        if (ExtentReportManager.getTest() != null) {
            ExtentReportManager.getTest().log(Status.INFO,
                    "<b>" + method + "</b> " + endpoint);

            if (params != null && !params.isEmpty()) {
                ExtentReportManager.getTest().log(Status.INFO,
                        "Request params: " + MarkupHelper.createCodeBlock(params.toString()));
            }

            int statusCode = response.statusCode();
            Status status = (statusCode >= 200 && statusCode < 300) ? Status.PASS : Status.WARNING;

            ExtentReportManager.getTest().log(status,
                    "Status: <b>" + statusCode + "</b>");
            ExtentReportManager.getTest().log(Status.INFO,
                    MarkupHelper.createCodeBlock(response.body().asPrettyString(), CodeLanguage.JSON));
        }
    }

    protected Response post(String endpoint, Map<String,Object> params){
        logger.info("POST {} with params: {}", endpoint, params);
        RequestSpecification spec=given().spec(RequestSpec.getBaseSpec());
        params.forEach((k,v)->spec.formParam(k, v));
        Response response = spec.when().post(endpoint);
        logger.info("Response status: {} | Body: {}", response.statusCode(), response.body().asString());
        logToReport("POST", endpoint, params, response);
        return response;
    }
    protected Response get(String endpoint) {
        logger.info("GET {}", endpoint);
        Response response = given()
                .spec(RequestSpec.getBaseSpec())
                .when()
                .get(endpoint);
        logger.info("Response status: {} | Body: {}", response.statusCode(), response.body().asString());
        logToReport("GET", endpoint, null, response);

        return response;
    }

    protected Response get(String endpoint, Map<String, Object> params) {
        logger.info("GET {} with query params: {}", endpoint, params);
        RequestSpecification spec = given().spec(RequestSpec.getBaseSpec());
        params.forEach((key, value) -> spec.queryParam(key, value));
        Response response = spec.when().get(endpoint);
        logger.info("Response status: {} | Body: {}", response.statusCode(), response.body().asString());
        logToReport("GET", endpoint, params, response);
        return response;
    }

    protected Response delete(String endpoint) {
        logger.info("DELETE {}", endpoint);
        Response response = given()
                .spec(RequestSpec.getBaseSpec())
                .when()
                .delete(endpoint);
        logger.info("Response status: {} | Body: {}", response.statusCode(), response.body().asString());
        logToReport("DELETE", endpoint, null, response);
        return response;
    }

    protected Response postWithIdempotencyKey(String endpoint, Map<String, Object> params, String idempotencyKey) {
        logger.info("POST {} with params: {} | Idempotency-Key: {}", endpoint, params, idempotencyKey);
        RequestSpecification spec = given().spec(RequestSpec.getBaseSpec());
        spec.header("Idempotency-Key", idempotencyKey);
        params.forEach((key, value) -> spec.formParam(key, value));
        Response response = spec.when().post(endpoint);
        logger.info("Response status: {} | Body: {}", response.statusCode(), response.body().asString());
        logToReport("POST", endpoint, params, response);
        return response;
    }


}
