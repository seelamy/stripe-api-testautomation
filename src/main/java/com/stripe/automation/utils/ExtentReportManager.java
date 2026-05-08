package com.stripe.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    public static ExtentReports getInstance() {
        if (extent == null) {
            ExtentSparkReporter spark = new ExtentSparkReporter("test-reports/extent-report.html");
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("Stripe API Test Report");
            spark.config().setReportName("Stripe API Automation");
            spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extent = new ExtentReports();
            extent.setSystemInfo("Framework", "RestAssured + TestNG");
            extent.setSystemInfo("API", "Stripe Sandbox");
            extent.setSystemInfo("Environment", "Test");
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.attachReporter(spark);
        }
        return extent;
    }

    public static void setTest(ExtentTest extentTest) {
        test.set(extentTest);
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}