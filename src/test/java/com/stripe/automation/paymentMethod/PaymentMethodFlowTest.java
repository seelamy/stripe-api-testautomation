package com.stripe.automation.paymentMethod;

import com.stripe.automation.base.BaseTest;
import com.stripe.automation.builders.CustomerBuilder;
import com.stripe.automation.models.Customer;
import com.stripe.automation.models.PaymentMethod;
import com.stripe.automation.spec.ResponseSpec;
import com.stripe.automation.utils.DataHelper;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentMethodFlowTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(PaymentMethodFlowTest.class);

    @Test(description = "Smoke: Complete PaymentMethod lifecycle — Attach → Retrieve → List → Detach → Verify")
    public void shouldCompletePaymentMethodLifecycle() {

        // ===== 1. CREATE CUSTOMER =====
        Map<String, Object> customerParams = CustomerBuilder.create()
                .withEmail(DataHelper.randomEmail())
                .withName(DataHelper.randomName())
                .build();

        Response customerResponse = customerService.create(customerParams);
        Customer customer = customerResponse.as(Customer.class);
        String customerId = customer.getId();
        trackCustomerId(customerId);
        logger.info("CREATE CUSTOMER — PASSED ✓ | {}", customerId);

        // ===== 2. ATTACH PAYMENT METHOD =====
        Response attachResponse = paymentMethodService.attach("pm_card_visa", customerId);
        attachResponse.then().spec(ResponseSpec.paymentMethodSuccess());

        PaymentMethod attached = attachResponse.as(PaymentMethod.class);
        String pmId = attached.getId();

        assertThat(pmId).startsWith("pm_");
        assertThat(attached.getCustomer()).isEqualTo(customerId);
        assertThat(attached.getType()).isEqualTo("card");
        assertThat(attached.getCard().getBrand()).isEqualTo("visa");
        assertThat(attached.getCard().getLast4()).isEqualTo("4242");
        assertThat(attached.getCard().getExpMonth()).isGreaterThan(0);
        assertThat(attached.getCard().getExpYear()).isGreaterThan(2024);
        logger.info("ATTACH PM — PASSED ✓ | {} | Visa ending {}", pmId, attached.getCard().getLast4());

        // ===== 3. RETRIEVE PAYMENT METHOD =====
        Response retrieveResponse = paymentMethodService.retrieve(pmId);
        retrieveResponse.then().spec(ResponseSpec.paymentMethodSuccess());

        PaymentMethod retrieved = retrieveResponse.as(PaymentMethod.class);

        assertThat(retrieved.getId()).isEqualTo(pmId);
        assertThat(retrieved.getCustomer()).isEqualTo(customerId);
        assertThat(retrieved.getCard().getBrand()).isEqualTo("visa");
        logger.info("RETRIEVE PM — PASSED ✓");

        // ===== 4. LIST CUSTOMER'S PAYMENT METHODS =====
        Response listResponse = paymentMethodService.listByCustomer(customerId);
        listResponse.then().spec(ResponseSpec.listSuccess());

        List<Map<String, Object>> pmList = listResponse.jsonPath().getList("data");

        assertThat(pmList).isNotEmpty();
        assertThat(pmList.size()).isGreaterThanOrEqualTo(1);

        boolean found = pmList.stream()
                .anyMatch(pm -> pmId.equals(pm.get("id")));
        assertThat(found)
                .as("Attached PM should appear in customer's payment method list")
                .isTrue();
        logger.info("LIST PMs — PASSED ✓ | Found {} payment methods", pmList.size());

        // ===== 5. DETACH PAYMENT METHOD =====
        Response detachResponse = paymentMethodService.detach(pmId);
        detachResponse.then().spec(ResponseSpec.paymentMethodSuccess());

        PaymentMethod detached = detachResponse.as(PaymentMethod.class);

        assertThat(detached.getId()).isEqualTo(pmId);
        assertThat(detached.getCustomer()).isNull();
        logger.info("DETACH PM — PASSED ✓ | customer is now null");

        // ===== 6. VERIFY DETACHED — NOT IN LIST =====
        Response listAfterDetach = paymentMethodService.listByCustomer(customerId);
        List<Map<String, Object>> pmListAfter = listAfterDetach.jsonPath().getList("data");

        boolean stillExists = pmListAfter.stream()
                .anyMatch(pm -> pmId.equals(pm.get("id")));
        assertThat(stillExists)
                .as("Detached PM should NOT appear in customer's list")
                .isFalse();
        logger.info("VERIFY DETACHED — PASSED ✓ | PM removed from list");

        // ===== 7. VERIFY DETACHED — RETRIEVE SHOWS NULL CUSTOMER =====
        Response retrieveAfterDetach = paymentMethodService.retrieve(pmId);
        PaymentMethod afterDetach = retrieveAfterDetach.as(PaymentMethod.class);

        assertThat(afterDetach.getCustomer()).isNull();
        assertThat(afterDetach.getCard().getBrand()).isEqualTo("visa");
        logger.info("VERIFY RETRIEVE AFTER DETACH — PASSED ✓ | PM exists but no customer");
    }
}