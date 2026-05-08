package com.stripe.automation.customer;

import com.stripe.automation.base.BaseTest;
import com.stripe.automation.builders.CustomerBuilder;
import com.stripe.automation.constants.StatusCodes;
import com.stripe.automation.models.Customer;
import com.stripe.automation.models.ErrorResponse;
import com.stripe.automation.utils.DataHelper;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteCustomerTest extends BaseTest {

    @Test(description = "Delete existing customer returns 200 with deleted flag")
    public void shouldDeleteExistingCustomer() {
        // Create a customer to delete
        Map<String, Object> params = CustomerBuilder.create()
                .withEmail(DataHelper.randomEmail())
                .build();

        Response createResponse = customerService.create(params);
        String customerId = createResponse.as(Customer.class).getId();

        // Delete
        Response deleteResponse = customerService.deleted(customerId);

        assertThat(deleteResponse.statusCode()).isEqualTo(StatusCodes.OK);
        assertThat(deleteResponse.jsonPath().getBoolean("deleted")).isTrue();
        assertThat(deleteResponse.jsonPath().getString("id")).isEqualTo(customerId);
    }

    @Test(description = "Delete same customer twice — second delete also returns 200")
    public void shouldHandleDoubleDelete() {
        // Create a customer
        Map<String, Object> params = CustomerBuilder.create()
                .withEmail(DataHelper.randomEmail())
                .build();

        Response createResponse = customerService.create(params);
        String customerId = createResponse.as(Customer.class).getId();

        // First delete
        Response firstDelete = customerService.deleted(customerId);
        assertThat(firstDelete.statusCode()).isEqualTo(StatusCodes.OK);

        // Second delete — should NOT throw error
        Response secondDelete = customerService.deleted(customerId);
        assertThat(secondDelete.statusCode()).isEqualTo(StatusCodes.OK);
        assertThat(secondDelete.jsonPath().getBoolean("deleted")).isTrue();
    }

    @Test(description = "Delete non-existent customer returns 404")
    public void shouldReturn404WhenDeletingNonExistentCustomer() {
        Response response = customerService.deleted("cus_doesnotexist");

        assertThat(response.statusCode()).isEqualTo(StatusCodes.NOT_FOUND);

        ErrorResponse error = response.as(ErrorResponse.class);
        assertThat(error.getError().getType()).isEqualTo("invalid_request_error");
    }
}