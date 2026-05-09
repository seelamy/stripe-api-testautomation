package com.stripe.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReportManager {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    public static void initReport(String suiteName) {
        if (extent == null) {
            String reportFile;
            String reportName;

            if (suiteName.toLowerCase().contains("smoke")) {
                reportFile = "test-reports/smoke-report.html";
                reportName = "Stripe API — Smoke Tests";
            } else {
                reportFile = "test-reports/regression-report.html";
                reportName = "Stripe API — Regression Tests";
            }

            ExtentSparkReporter spark = new ExtentSparkReporter(reportFile);
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle(reportName);
            spark.config().setReportName(reportName);
            spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extent = new ExtentReports();
            extent.setSystemInfo("Framework", "RestAssured + TestNG");
            extent.setSystemInfo("API", "Stripe Sandbox");
            extent.setSystemInfo("Suite", suiteName);
            extent.setSystemInfo("Environment", "Test");
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.attachReporter(spark);
        }
    }
    public static ExtentReports getInstance() {
        if (extent == null) {
            initReport("Regression Suite");
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