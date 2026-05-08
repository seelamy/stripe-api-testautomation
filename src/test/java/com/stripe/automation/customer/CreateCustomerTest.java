package com.stripe.automation.customer;

import com.stripe.automation.base.BaseTest;
import com.stripe.automation.builders.CustomerBuilder;
import com.stripe.automation.models.Customer;
import com.stripe.automation.models.ErrorResponse;
import com.stripe.automation.spec.ResponseSpec;
import com.stripe.automation.utils.DataHelper;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

public class CreateCustomerTest extends BaseTest {
    @Test(description = "TC-01: Create customer with all valid fields and verify via GET")
    public void shouldCreateCustomerWithAllFields() {
        String email = DataHelper.randomEmail();
        String name = DataHelper.randomName();
        String phone = DataHelper.randomPhone();
        String description = DataHelper.randomDescription();
        Map<String, Object> params = CustomerBuilder.create()
                .withEmail(email)
                .withName(name)
                .withPhone(phone)
                .withDescription(description)
                .withAddressLine1("123 Main Street")
                .withCity("San Francisco")
                .withState("CA")
                .withPostalCode("94105")
                .withCountry("US")
                .withMetadata("source", "automation")
                .withMetadata("env", "test")
                .build();
        // Create
        Response createResponse = customerService.create(params);
        createResponse.then().spec(ResponseSpec.customerSuccess());

        Customer created = createResponse.as(Customer.class);
        trackCustomerId(created.getId());

        assertThat(DataHelper.isValidCustomerId(created.getId())).isTrue();
        assertThat(created.getEmail()).isEqualTo(email);
        assertThat(created.getName()).isEqualTo(name);
        assertThat(created.getPhone()).isEqualTo(phone);
        assertThat(created.getDescription()).isEqualTo(description);
        assertThat(created.getAddress().getLine1()).isEqualTo("123 Main Street");
        assertThat(created.getAddress().getCity()).isEqualTo("San Francisco");
        assertThat(created.getAddress().getState()).isEqualTo("CA");
        assertThat(created.getAddress().getPostalCode()).isEqualTo("94105");
        assertThat(created.getAddress().getCountry()).isEqualTo("US");
        assertThat(created.getMetadata()).containsEntry("source", "automation");
        assertThat(created.getMetadata()).containsEntry("env", "test");
        assertThat(created.getCreated()).isGreaterThan(0);
        assertThat(created.isLivemode()).isFalse();

        // Verify via GET
        Response getResponse = customerService.retrieve(created.getId());
        getResponse.then().spec(ResponseSpec.customerSuccess());

        Customer retrieved = getResponse.as(Customer.class);
        assertThat(retrieved.getId()).isEqualTo(created.getId());
        assertThat(retrieved.getEmail()).isEqualTo(email);
        assertThat(retrieved.getName()).isEqualTo(name);
        assertThat(retrieved.getPhone()).isEqualTo(phone);
        assertThat(retrieved.getAddress().getCity()).isEqualTo("San Francisco");
        assertThat(retrieved.getMetadata()).containsEntry("source", "automation");
    }

    @Test(description = "TC-02: Create customer with only email, verify other fields are null")
    public void shouldCreateCustomerWithOnlyEmail() {
        String email = DataHelper.randomEmail();

        Map<String, Object> params = CustomerBuilder.create()
                .withEmail(email)
                .build();

        Response createResponse = customerService.create(params);
        createResponse.then().spec(ResponseSpec.customerSuccess());

        Customer created = createResponse.as(Customer.class);
        trackCustomerId(created.getId());

        assertThat(created.getEmail()).isEqualTo(email);
        assertThat(created.getName()).isNull();
        assertThat(created.getPhone()).isNull();
        assertThat(created.getDescription()).isNull();
        assertThat(created.getAddress()).isNull();
        assertThat(created.getMetadata()).isEmpty();

        // Verify via GET
        Response getResponse = customerService.retrieve(created.getId());
        Customer retrieved = getResponse.as(Customer.class);

        assertThat(retrieved.getEmail()).isEqualTo(email);
        assertThat(retrieved.getName()).isNull();
        assertThat(retrieved.getMetadata()).isEmpty();
    }

    @Test(description = "TC-03: Create customer with empty body, verify customer still created")
    public void shouldCreateCustomerWithEmptyBody() {
        Map<String, Object> params = CustomerBuilder.create().build();

        Response createResponse = customerService.create(params);
        createResponse.then().spec(ResponseSpec.customerSuccess());

        Customer created = createResponse.as(Customer.class);
        trackCustomerId(created.getId());

        assertThat(DataHelper.isValidCustomerId(created.getId())).isTrue();
        assertThat(created.getEmail()).isNull();
        assertThat(created.getName()).isNull();

        // Verify via GET
        Response getResponse = customerService.retrieve(created.getId());
        Customer retrieved = getResponse.as(Customer.class);

        assertThat(retrieved.getId()).isEqualTo(created.getId());
        assertThat(retrieved.getEmail()).isNull();
    }

    @Test(description = "TC-04: Create customer with special characters in name")
    public void shouldCreateCustomerWithSpecialCharacters() {
        String name = "José García-López O'Brien";
        String email = DataHelper.randomEmail();

        Map<String, Object> params = CustomerBuilder.create()
                .withEmail(email)
                .withName(name)
                .build();

        Response createResponse = customerService.create(params);
        createResponse.then().spec(ResponseSpec.customerSuccess());

        Customer created = createResponse.as(Customer.class);
        trackCustomerId(created.getId());

        assertThat(created.getName()).isEqualTo(name);

        // Verify via GET — name not corrupted
        Response getResponse = customerService.retrieve(created.getId());
        Customer retrieved = getResponse.as(Customer.class);

        assertThat(retrieved.getName()).isEqualTo(name);
    }

    @Test(description = "TC-05: Create two customers with same email, verify different IDs")
    public void shouldCreateCustomerWithDuplicateEmail() {
        String email = DataHelper.randomEmail();

        Map<String, Object> params1 = CustomerBuilder.create()
                .withEmail(email)
                .build();

        Map<String, Object> params2 = CustomerBuilder.create()
                .withEmail(email)
                .build();

        Response response1 = customerService.create(params1);
        Response response2 = customerService.create(params2);

        Customer customer1 = response1.as(Customer.class);
        Customer customer2 = response2.as(Customer.class);
        trackCustomerId(customer1.getId());
        trackCustomerId(customer2.getId());

        // Different IDs — Stripe allows duplicate emails
        assertThat(customer1.getId()).isNotEqualTo(customer2.getId());

        // Both have same email
        assertThat(customer1.getEmail()).isEqualTo(email);
        assertThat(customer2.getEmail()).isEqualTo(email);

        // Verify both exist via GET
        Response get1 = customerService.retrieve(customer1.getId());
        Response get2 = customerService.retrieve(customer2.getId());

        assertThat(get1.as(Customer.class).getEmail()).isEqualTo(email);
        assertThat(get2.as(Customer.class).getEmail()).isEqualTo(email);
    }
    @Test(description = "TC-06: Create customer with 50 metadata keys (max limit)")
    public void shouldCreateCustomerWithMaxMetadata() {
        Map<String, Object> params = CustomerBuilder.create()
                .withEmail(DataHelper.randomEmail())
                .withMetadataMap(DataHelper.randomMetadata(50))
                .build();

        Response createResponse = customerService.create(params);
        createResponse.then().spec(ResponseSpec.customerSuccess());

        Customer created = createResponse.as(Customer.class);
        trackCustomerId(created.getId());

        assertThat(created.getMetadata()).hasSize(50);

        // Verify via GET
        Response getResponse = customerService.retrieve(created.getId());
        Customer retrieved = getResponse.as(Customer.class);

        assertThat(retrieved.getMetadata()).hasSize(50);
    }

    @Test(description = "TC-07: Create customer with invalid country code returns 400")
    public void shouldRejectInvalidCountryCode() {
        Map<String, Object> params = CustomerBuilder.create()
                .withEmail(DataHelper.randomEmail())
                .withCountry("USA")
                .build();

        Response response = customerService.create(params);
        response.then().spec(ResponseSpec.badRequest());

        ErrorResponse error = response.as(ErrorResponse.class);

        assertThat(error.getError().getType()).isEqualTo("invalid_request_error");
    }

}
