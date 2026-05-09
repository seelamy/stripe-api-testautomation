package com.stripe.automation.utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentTestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        String suiteName = context.getSuite().getName();
        ExtentReportManager.initReport(suiteName);
    }

    @Override
    public void onTestStart(ITestResult result) {
        String description = result.getMethod().getDescription();
        String testName = description != null && !description.isEmpty()
                ? description
                : result.getMethod().getMethodName();

        String className = result.getTestClass().getName();
        String simpleClassName = className.substring(className.lastIndexOf('.') + 1);

        ExtentTest extentTest = ExtentReportManager.getInstance()
                .createTest(testName)
                .assignCategory(simpleClassName);

        ExtentReportManager.setTest(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.getTest().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentReportManager.getTest().log(Status.FAIL,
                "Test failed: " + result.getThrowable().getMessage());

        String stackTrace = getFilteredStackTrace(result.getThrowable());
        ExtentReportManager.getTest().log(Status.FAIL, "<pre>" + stackTrace + "</pre>");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String reason = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "Dependency failed";

        ExtentReportManager.getTest().log(Status.SKIP, "Test skipped: " + reason);
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flush();
    }

    private String getFilteredStackTrace(Throwable t) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.toString()).append("\n");
        for (StackTraceElement element : t.getStackTrace()) {
            if (element.getClassName().contains("stripe.automation")) {
                sb.append("  at ").append(element).append("\n");
            }
        }
        return sb.toString();
    }
}