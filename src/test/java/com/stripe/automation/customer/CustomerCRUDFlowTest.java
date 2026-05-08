package com.stripe.automation.customer;

import com.stripe.automation.base.BaseTest;
import com.stripe.automation.builders.CustomerBuilder;
import com.stripe.automation.constants.StatusCodes;
import com.stripe.automation.models.Customer;
import com.stripe.automation.spec.ResponseSpec;
import com.stripe.automation.utils.DataHelper;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerCRUDFlowTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CustomerCRUDFlowTest.class);

    @Test(description = "Smoke: Complete Customer CRUD lifecycle")
    public void shouldCompleteCustomerCRUDLifecycle() {

        // ===== CREATE =====
        String email = DataHelper.randomEmail();
        String name = DataHelper.randomName();
        String phone = DataHelper.randomPhone();

        Map<String, Object> createParams = CustomerBuilder.create()
                .withEmail(email)
                .withName(name)
                .withPhone(phone)
                .withMetadata("source", "smoke_test")
                .build();

        Response createResponse = customerService.create(createParams);
        createResponse.then().spec(ResponseSpec.customerSuccess());

        Customer created = createResponse.as(Customer.class);
        String customerId = created.getId();
        trackCustomerId(customerId);

        assertThat(DataHelper.isValidCustomerId(customerId)).isTrue();
        assertThat(created.getEmail()).isEqualTo(email);
        assertThat(created.getName()).isEqualTo(name);
        assertThat(created.getPhone()).isEqualTo(phone);
        logger.info("CREATE — PASSED ✓");

        // ===== RETRIEVE AFTER CREATE =====
        Response getResponse = customerService.retrieve(customerId);
        getResponse.then().spec(ResponseSpec.customerSuccess());

        Customer retrieved = getResponse.as(Customer.class);

        assertThat(retrieved.getId()).isEqualTo(customerId);
        assertThat(retrieved.getEmail()).isEqualTo(email);
        assertThat(retrieved.getName()).isEqualTo(name);
        assertThat(retrieved.getPhone()).isEqualTo(phone);
        logger.info("RETRIEVE AFTER CREATE — PASSED ✓");

        // ===== UPDATE =====
        String updatedEmail = DataHelper.randomEmail();

        Map<String, Object> updateParams = CustomerBuilder.create()
                .withEmail(updatedEmail)
                .withMetadata("tier", "premium")
                .build();

        Response updateResponse = customerService.update(customerId, updateParams);
        updateResponse.then().spec(ResponseSpec.customerSuccess());

        Customer updated = updateResponse.as(Customer.class);

        assertThat(updated.getEmail()).isEqualTo(updatedEmail);
        assertThat(updated.getName()).isEqualTo(name);
        assertThat(updated.getMetadata()).containsEntry("source", "smoke_test");
        assertThat(updated.getMetadata()).containsEntry("tier", "premium");
        logger.info("UPDATE — PASSED ✓");

        // ===== RETRIEVE AFTER UPDATE =====
        Response getAfterUpdate = customerService.retrieve(customerId);
        Customer afterUpdate = getAfterUpdate.as(Customer.class);

        assertThat(afterUpdate.getEmail()).isEqualTo(updatedEmail);
        assertThat(afterUpdate.getName()).isEqualTo(name);
        assertThat(afterUpdate.getMetadata()).hasSize(2);
        logger.info("RETRIEVE AFTER UPDATE — PASSED ✓");

        // ===== DELETE =====
        Response deleteResponse = customerService.deleted(customerId);

        assertThat(deleteResponse.statusCode()).isEqualTo(StatusCodes.OK);
        assertThat(deleteResponse.jsonPath().getBoolean("deleted")).isTrue();
        logger.info("DELETE — PASSED ✓");

        // ===== RETRIEVE AFTER DELETE =====
        Response getAfterDelete = customerService.retrieve(customerId);

        assertThat(getAfterDelete.statusCode()).isEqualTo(StatusCodes.OK);
        Customer afterDelete = getAfterDelete.as(Customer.class);
        assertThat(afterDelete.getDeleted()).isTrue();
        assertThat(afterDelete.getId()).isEqualTo(customerId);
        logger.info("RETRIEVE AFTER DELETE — PASSED ✓");
    }
}