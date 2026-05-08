package com.stripe.automation.customer;

import com.stripe.automation.base.BaseTest;
import com.stripe.automation.builders.CustomerBuilder;
import com.stripe.automation.constants.StatusCodes;
import com.stripe.automation.models.Customer;
import com.stripe.automation.models.ErrorResponse;
import com.stripe.automation.spec.ResponseSpec;
import com.stripe.automation.utils.DataHelper;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateCustomerTest extends BaseTest {

    private String customerId;
    private String originalEmail;

    @BeforeClass
    public void createTestCustomer() {
        super.setup();
        originalEmail = DataHelper.randomEmail();

        Map<String, Object> params = CustomerBuilder.create()
                .withEmail(originalEmail)
                .withName("Original Name")
                .withMetadata("key1", "value1")
                .withMetadata("key2", "value2")
                .build();

        Response response = customerService.create(params);
        customerId = response.as(Customer.class).getId();
        trackCustomerId(customerId);
    }

    @Test(description = "Update single field, verify other fields unchanged")
    public void shouldUpdateSingleFieldWithoutAffectingOthers() {
        Map<String, Object> params = CustomerBuilder.create()
                .withName("Updated Name")
                .build();

        Response response = customerService.update(customerId, params);
        response.then().spec(ResponseSpec.customerSuccess());

        Customer customer = response.as(Customer.class);

        assertThat(customer.getName()).isEqualTo("Updated Name");
        assertThat(customer.getEmail()).isEqualTo(originalEmail);
        assertThat(customer.getMetadata()).containsEntry("key1", "value1");
    }

    @Test(description = "Metadata merge — new keys added, old keys preserved")
    public void shouldMergeMetadataOnUpdate() {
        Map<String, Object> params = CustomerBuilder.create()
                .withMetadata("key3", "value3")
                .build();

        Response response = customerService.update(customerId, params);
        Customer customer = response.as(Customer.class);

        assertThat(customer.getMetadata()).containsEntry("key1", "value1");
        assertThat(customer.getMetadata()).containsEntry("key2", "value2");
        assertThat(customer.getMetadata()).containsEntry("key3", "value3");
    }

    @Test(description = "Delete metadata key by sending empty value")
    public void shouldDeleteMetadataKeyWithEmptyValue() {
        Map<String, Object> params = CustomerBuilder.create()
                .withMetadata("key2", "")
                .build();

        Response response = customerService.update(customerId, params);
        Customer customer = response.as(Customer.class);

        assertThat(customer.getMetadata()).containsKey("key1");
        assertThat(customer.getMetadata()).doesNotContainKey("key2");
    }

    @Test(description = "Update non-existent customer returns 404")
    public void shouldReturn404WhenUpdatingNonExistentCustomer() {
        Map<String, Object> params = CustomerBuilder.create()
                .withName("Ghost")
                .build();

        Response response = customerService.update("cus_doesnotexist", params);

        assertThat(response.statusCode()).isEqualTo(StatusCodes.NOT_FOUND);

        ErrorResponse error = response.as(ErrorResponse.class);
        assertThat(error.getError().getType()).isEqualTo("invalid_request_error");
    }
}