package com.stripe.automation.payment;

import com.stripe.automation.base.BaseTest;
import com.stripe.automation.builders.CustomerBuilder;
import com.stripe.automation.builders.PaymentIntentBuilder;
import com.stripe.automation.models.Customer;
import com.stripe.automation.models.PaymentIntent;
import com.stripe.automation.spec.ResponseSpec;
import com.stripe.automation.utils.DataHelper;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PaymentIntentTest extends BaseTest {

    private String customerId;
    private String paymentMethodId;

    @BeforeClass
    public void createTestCustomerWithCard() {
        super.setup();

        Map<String, Object> customerParams = CustomerBuilder.create()
                .withEmail(DataHelper.randomEmail())
                .withName(DataHelper.randomName())
                .build();

        Response customerResponse = customerService.create(customerParams);
        customerId = customerResponse.as(Customer.class).getId();
        trackCustomerId(customerId);

        Response attachResponse = paymentMethodService.attach("pm_card_visa", customerId);
        paymentMethodId = attachResponse.jsonPath().getString("id");
    }

    @Test(description = "TC-02: Create and confirm PaymentIntent in one call")
    public void shouldCreateAndConfirmInOneCall() {
        Map<String, Object> params = PaymentIntentBuilder.create()
                .withAmount(2000)
                .withCurrency("usd")
                .withCustomer(customerId)
                .withPaymentMethod(paymentMethodId)
                .withConfirm(true)
                .withAutomaticPaymentMethods(true)
                .build();

        Response response = paymentIntentService.create(params);
        response.then().spec(ResponseSpec.paymentIntentSucceeded());

        PaymentIntent pi = response.as(PaymentIntent.class);

        assertThat(pi.getAmount()).isEqualTo(2000);
        assertThat(pi.getCurrency()).isEqualTo("usd");
        assertThat(pi.getAmountReceived()).isEqualTo(2000);
        assertThat(pi.getStatus()).isEqualTo("succeeded");
        assertThat(pi.getCustomer()).isEqualTo(customerId);
    }

    @Test(description = "TC-03: Create then confirm separately")
    public void shouldCreateThenConfirmSeparately() {
        Map<String, Object> createParams = PaymentIntentBuilder.create()
                .withAmount(3000)
                .withCurrency("usd")
                .withCustomer(customerId)
                .withAutomaticPaymentMethods(true)
                .build();

        Response createResponse = paymentIntentService.create(createParams);
        createResponse.then().spec(ResponseSpec.paymentIntentSuccess());

        PaymentIntent created = createResponse.as(PaymentIntent.class);
        assertThat(created.getStatus()).isEqualTo("requires_payment_method");

        Map<String, Object> confirmParams = Map.of("payment_method", paymentMethodId);
        Response confirmResponse = paymentIntentService.confirm(created.getId(), confirmParams);

        PaymentIntent confirmed = confirmResponse.as(PaymentIntent.class);
        assertThat(confirmed.getStatus()).isEqualTo("succeeded");
        assertThat(confirmed.getAmountReceived()).isEqualTo(3000);
    }

    @Test(description = "TC-04: Create with manual capture then capture")
    public void shouldAuthorizeAndCaptureSeparately() {
        Map<String, Object> params = PaymentIntentBuilder.create()
                .withAmount(4000)
                .withCurrency("usd")
                .withCustomer(customerId)
                .withPaymentMethod(paymentMethodId)
                .withConfirm(true)
                .withCaptureMethod("manual")
                .withAutomaticPaymentMethods(true)
                .build();

        Response createResponse = paymentIntentService.create(params);
        PaymentIntent authorized = createResponse.as(PaymentIntent.class);

        assertThat(authorized.getStatus()).isEqualTo("requires_capture");
        assertThat(authorized.getAmount()).isEqualTo(4000);

        Response captureResponse = paymentIntentService.capture(authorized.getId());
        PaymentIntent captured = captureResponse.as(PaymentIntent.class);

        assertThat(captured.getStatus()).isEqualTo("succeeded");
        assertThat(captured.getAmountReceived()).isEqualTo(4000);
    }

    @Test(description = "TC-05: Partial capture — capture less than authorized")
    public void shouldPartialCapture() {
        Map<String, Object> params = PaymentIntentBuilder.create()
                .withAmount(5000)
                .withCurrency("usd")
                .withCustomer(customerId)
                .withPaymentMethod(paymentMethodId)
                .withConfirm(true)
                .withCaptureMethod("manual")
                .withAutomaticPaymentMethods(true)
                .build();

        Response createResponse = paymentIntentService.create(params);
        PaymentIntent authorized = createResponse.as(PaymentIntent.class);

        assertThat(authorized.getStatus()).isEqualTo("requires_capture");

        Map<String, Object> captureParams = Map.of("amount_to_capture", 3000);
        Response captureResponse = paymentIntentService.capture(authorized.getId(), captureParams);
        PaymentIntent captured = captureResponse.as(PaymentIntent.class);

        assertThat(captured.getStatus()).isEqualTo("succeeded");
        assertThat(captured.getAmountReceived()).isEqualTo(3000);
    }

    @Test(description = "TC-06: Cancel a PaymentIntent")
    public void shouldCancelPaymentIntent() {
        Map<String, Object> params = PaymentIntentBuilder.create()
                .withAmount(1000)
                .withCurrency("usd")
                .withCustomer(customerId)
                .withAutomaticPaymentMethods(true)
                .build();

        Response createResponse = paymentIntentService.create(params);
        PaymentIntent created = createResponse.as(PaymentIntent.class);

        assertThat(created.getStatus()).isEqualTo("requires_payment_method");

        Response cancelResponse = paymentIntentService.cancel(created.getId());
        PaymentIntent canceled = cancelResponse.as(PaymentIntent.class);

        assertThat(canceled.getStatus()).isEqualTo("canceled");
    }

    @Test(description = "TC-07: Create PaymentIntent with metadata")
    public void shouldCreateWithMetadata() {
        Map<String, Object> params = PaymentIntentBuilder.create()
                .withAmount(2500)
                .withCurrency("usd")
                .withCustomer(customerId)
                .withPaymentMethod(paymentMethodId)
                .withConfirm(true)
                .withAutomaticPaymentMethods(true)
                .withMetadata("order_id", "ORD-123")
                .withMetadata("product", "premium_plan")
                .build();

        Response response = paymentIntentService.create(params);
        PaymentIntent pi = response.as(PaymentIntent.class);

        assertThat(pi.getStatus()).isEqualTo("succeeded");
        assertThat(pi.getMetadata()).containsEntry("order_id", "ORD-123");
        assertThat(pi.getMetadata()).containsEntry("product", "premium_plan");
    }

    @Test(description = "TC-08: Create PaymentIntent with EUR currency")
    public void shouldCreateWithDifferentCurrency() {
        Map<String, Object> params = PaymentIntentBuilder.create()
                .withAmount(1500)
                .withCurrency("eur")
                .withCustomer(customerId)
                .withPaymentMethod(paymentMethodId)
                .withConfirm(true)
                .withAutomaticPaymentMethods(true)
                .build();

        Response response = paymentIntentService.create(params);
        PaymentIntent pi = response.as(PaymentIntent.class);

        assertThat(pi.getStatus()).isEqualTo("succeeded");
        assertThat(pi.getCurrency()).isEqualTo("eur");
        assertThat(pi.getAmount()).isEqualTo(1500);
    }

    @Test(description = "TC-09: Idempotent create — same key returns same PaymentIntent")
    public void shouldReturnSamePaymentIntentForSameIdempotencyKey() {
        String idempotencyKey = DataHelper.idempotencyKey();

        Map<String, Object> params = PaymentIntentBuilder.create()
                .withAmount(2000)
                .withCurrency("usd")
                .withCustomer(customerId)
                .withPaymentMethod(paymentMethodId)
                .withConfirm(true)
                .withAutomaticPaymentMethods(true)
                .build();

        Response firstResponse = paymentIntentService.createWithIdempotencyKey(params, idempotencyKey);
        Response secondResponse = paymentIntentService.createWithIdempotencyKey(params, idempotencyKey);

        PaymentIntent first = firstResponse.as(PaymentIntent.class);
        PaymentIntent second = secondResponse.as(PaymentIntent.class);

        assertThat(first.getId()).isEqualTo(second.getId());
        assertThat(first.getAmount()).isEqualTo(second.getAmount());
    }
}