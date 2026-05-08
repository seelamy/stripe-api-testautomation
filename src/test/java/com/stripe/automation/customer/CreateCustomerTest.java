package com.stripe.automation.customer;

import com.stripe.automation.base.BaseTest;
import com.stripe.automation.builders.CustomerBuilder;
import com.stripe.automation.models.Customer;
import com.stripe.automation.spec.ResponseSpec;
import com.stripe.automation.utils.DataHelper;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

public class CreateCustomerTest extends BaseTest {
    @Test(description = "TC-01: Create customer with all valid fields")
    public void testCreateCustomerWithAllFields(){
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
        Response response = customerService.create(params);
        System.out.println("STATUS: " + response.statusCode());
        System.out.println("BODY: " + response.body().asString());
        response.then().spec(ResponseSpec.customerSuccess());

        Customer customer = response.as(Customer.class);
        trackCustomerId(customer.getId());

        assertThat(customer.getEmail()).isEqualTo(email);
        assertThat(customer.getName()).isEqualTo(name);
        assertThat(customer.getPhone()).isEqualTo(phone);
        assertThat(customer.getDescription()).isEqualTo(description);
        assertThat(customer.getAddress().getLine1()).isEqualTo("123 Main Street");
        assertThat(customer.getAddress().getCity()).isEqualTo("San Francisco");
        assertThat(customer.getAddress().getState()).isEqualTo("CA");
        assertThat(customer.getAddress().getPostalCode()).isEqualTo("94105");
        assertThat(customer.getAddress().getCountry()).isEqualTo("US");
        assertThat(customer.getMetadata()).containsEntry("source", "automation");
        assertThat(customer.getMetadata()).containsEntry("env", "test");
        assertThat(customer.getCreated()).isGreaterThan(0);
    }
}
