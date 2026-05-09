package com.stripe.automation.payment;

import com.stripe.automation.base.BaseTest;
import com.stripe.automation.builders.CustomerBuilder;
import com.stripe.automation.builders.PaymentIntentBuilder;
import com.stripe.automation.builders.RefundBuilder;
import com.stripe.automation.constants.StatusCodes;
import com.stripe.automation.models.Customer;
import com.stripe.automation.models.PaymentIntent;
import com.stripe.automation.models.Refund;
import com.stripe.automation.spec.ResponseSpec;
import com.stripe.automation.utils.DataHelper;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

public class PaymentFlowTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(PaymentFlowTest.class);
    @Test(description = "Smoke: Complete Payment lifecycle — Customer → PM → Charge → Refund")
    public void shouldCompletePaymentLifecycle(){
        // ===== 1. CREATE CUSTOMER =====
        logger.info("CREATE CUSTOMER");
        Map<String, Object> customerParams = CustomerBuilder.create()
                .withEmail(DataHelper.randomEmail())
                .withName(DataHelper.randomName())
                .build();

        Response customerResponse = customerService.create(customerParams);
        customerResponse.then().spec(ResponseSpec.customerSuccess());

        Customer customer = customerResponse.as(Customer.class);
        String customerId = customer.getId();
        trackCustomerId(customerId);

        assertThat(DataHelper.isValidCustomerId(customerId)).isTrue();
        logger.info("CREATE CUSTOMER — PASSED ✓ | {}", customerId);

        // ===== 2. ATTACH PAYMENT METHOD =====
        logger.info("ATTACH PAYMENT METHOD TO CUSTOMER");
        Response attachResponse = paymentMethodService.attach("pm_card_visa", customerId);
        attachResponse.then().spec(ResponseSpec.paymentMethodSuccess());
        String paymentMethodId = attachResponse.jsonPath().getString("id");
        assertThat(paymentMethodId).startsWith("pm_");
        assertThat(attachResponse.jsonPath().getString("customer")).isEqualTo(customerId);
        logger.info("ATTACH PAYMENT METHOD — PASSED ✓ | {}", paymentMethodId);

        // ===== 3. VERIFY PM IS ATTACHED =====
        logger.info("VERIFY PAYMENT METHOD ATTACHED TO CUSTOMER");
        Response listPmResponse = paymentMethodService.listByCustomer(customerId);
        listPmResponse.then().spec(ResponseSpec.listSuccess());

        assertThat(listPmResponse.jsonPath().getList("data")).isNotEmpty();
        logger.info("VERIFY PM ATTACHED — PASSED ✓");

        // ===== 4. CREATE PAYMENT INTENT =====
        logger.info("CREATE PAYMENT INTENT");
        Map<String, Object> paymentParams = PaymentIntentBuilder.create()
                .withAmount(2000)
                .withCurrency("usd")
                .withCustomer(customerId)
                .withPaymentMethod(paymentMethodId)
                .withConfirm(true)
                .withAutomaticPaymentMethods(true)
                .withDescription("Smoke test payment")
                .withMetadata("test_type", "smoke")
                .build();

        Response paymentResponse = paymentIntentService.create(paymentParams);
        paymentResponse.then().spec(ResponseSpec.paymentIntentSucceeded());

        PaymentIntent payment = paymentResponse.as(PaymentIntent.class);
        String paymentIntentId = payment.getId();

        assertThat(DataHelper.isValidPaymentIntentId(paymentIntentId)).isTrue();
        assertThat(payment.getAmount()).isEqualTo(2000);
        assertThat(payment.getCurrency()).isEqualTo("usd");
        assertThat(payment.getAmountReceived()).isEqualTo(2000);
        assertThat(payment.getCustomer()).isEqualTo(customerId);
        assertThat(payment.getStatus()).isEqualTo("succeeded");
        assertThat(payment.getMetadata()).containsEntry("test_type", "smoke");
        logger.info("CREATE PAYMENT INTENT — PASSED ✓ | {} | $20.00", paymentIntentId);

        // ===== 5. VERIFY PAYMENT VIA GET =====
        Response getPaymentResponse = paymentIntentService.retrieve(paymentIntentId);
        PaymentIntent retrieved = getPaymentResponse.as(PaymentIntent.class);

        assertThat(retrieved.getStatus()).isEqualTo("succeeded");
        assertThat(retrieved.getAmountReceived()).isEqualTo(2000);
        logger.info("VERIFY PAYMENT — PASSED ✓");

        // ===== 6. FULL REFUND =====
        Map<String, Object> refundParams = RefundBuilder.create()
                .withPaymentIntent(paymentIntentId)
                .withReason("requested_by_customer")
                .build();

        Response refundResponse = refundService.create(refundParams);
        refundResponse.then().spec(ResponseSpec.refundSuccess());

        Refund refund = refundResponse.as(Refund.class);

        assertThat(DataHelper.isValidRefundId(refund.getId())).isTrue();
        assertThat(refund.getAmount()).isEqualTo(2000);
        assertThat(refund.getStatus()).isEqualTo("succeeded");
        assertThat(refund.getPaymentIntent()).isEqualTo(paymentIntentId);
        assertThat(refund.getReason()).isEqualTo("requested_by_customer");
        logger.info("FULL REFUND — PASSED ✓ | {} | $20.00", refund.getId());

        // ===== 7. VERIFY PAYMENT AFTER REFUND =====
        Response afterRefund = paymentIntentService.retrieve(paymentIntentId);
        PaymentIntent afterRefundPI = afterRefund.as(PaymentIntent.class);

        assertThat(afterRefundPI.getStatus()).isEqualTo("succeeded");
        logger.info("VERIFY PAYMENT AFTER REFUND — PASSED ✓");

        // ===== 8. DETACH PAYMENT METHOD =====
        Response detachResponse = paymentMethodService.detach(paymentMethodId);

        assertThat(detachResponse.statusCode()).isEqualTo(StatusCodes.OK);
        assertThat(detachResponse.jsonPath().getString("customer")).isNull();
        logger.info("DETACH PAYMENT METHOD — PASSED ✓");
    }

}
