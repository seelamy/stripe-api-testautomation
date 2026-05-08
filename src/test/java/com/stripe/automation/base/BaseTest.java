package com.stripe.automation.base;

import com.stripe.automation.services.CustomerService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.ArrayList;
import java.util.List;

public class BaseTest {
    protected CustomerService customerService;
    protected List<String> createdCustomerIds;

    @BeforeClass
    public void setup() {
        customerService = new CustomerService();
        createdCustomerIds = new ArrayList<>();
    }

    protected void trackCustomerId(String id) {
        createdCustomerIds.add(id);
    }

    @AfterClass
    public void cleanup() {
        for (String id : createdCustomerIds) {
            try {
                customerService.deleted(id);
            } catch (Exception e) {
                // ignore cleanup failures
            }
        }
    }
}
