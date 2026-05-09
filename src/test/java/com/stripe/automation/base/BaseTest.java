package com.stripe.automation.base;

import com.stripe.automation.models.Refund;
import com.stripe.automation.services.CustomerService;
import com.stripe.automation.services.PaymentIntentService;
import com.stripe.automation.services.PaymentMethodService;
import com.stripe.automation.services.RefundService;
import com.stripe.automation.utils.ExtentTestListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
@Listeners(ExtentTestListener.class)
public class BaseTest {
    protected CustomerService customerService;
    protected PaymentMethodService paymentMethodService;
    protected PaymentIntentService paymentIntentService;
    protected RefundService refundService;
    protected List<String> createdCustomerIds;
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    @BeforeClass
    public void setup() {
        logger.info("========== TEST SETUP ==========");
        customerService = new CustomerService();
        paymentMethodService = new PaymentMethodService();
        paymentIntentService = new PaymentIntentService();
        refundService = new RefundService();
        createdCustomerIds = new ArrayList<>();
        logger.info("CustomerService initialized");

    }


    protected void trackCustomerId(String id) {
        createdCustomerIds.add(id);
        logger.info("Tracking customer for cleanup: {}", id);

    }
    @BeforeMethod
    public void beforeTest(Method method) {
        Test testAnnotation = method.getAnnotation(Test.class);
        if (testAnnotation != null && !testAnnotation.description().isEmpty()) {
            logger.info("---------- STARTING: {} ----------", testAnnotation.description());
        } else {
            logger.info("---------- STARTING: {} ----------", method.getName());
        }
    }

    @AfterMethod
    public void afterTest(ITestResult result) {
        String status = switch (result.getStatus()) {
            case ITestResult.SUCCESS -> "PASSED ✓";
            case ITestResult.FAILURE -> "FAILED ✗";
            case ITestResult.SKIP -> "SKIPPED ⊘";
            default -> "UNKNOWN";
        };

        String description = result.getMethod().getDescription();
        if (description != null && !description.isEmpty()) {
            logger.info("---------- FINISHED: {} — {} ----------", description, status);
        } else {
            logger.info("---------- FINISHED: {} — {} ----------", result.getMethod().getMethodName(), status);
        }
    }
    @AfterClass
    public void cleanup() {
        logger.info("========== TEST CLEANUP ==========");
        logger.info("Cleaning up {} customers", createdCustomerIds.size());
        for (String id : createdCustomerIds) {
            try {
                customerService.deleted(id);
                logger.info("Deleted customer: {}", id);
            } catch (Exception e) {
                logger.warn("Failed to delete customer {}: {}", id, e.getMessage());
            }
        }
    }
}
