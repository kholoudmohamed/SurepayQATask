package com.surepay.framework.reporting;

import org.testng.*;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentReportListener implements ITestListener, ISuiteListener {

	private static ExtentReports extentReports;
	private static ExtentTest extentTest;

	@Override
	public void onStart(ISuite suite) {
		String reportPath = System.getProperty("user.dir") + "/target/extent-reports/ExtentReport.html";
		ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
		sparkReporter.config().setDocumentTitle("SurePay API Test Report");
		sparkReporter.config().setReportName("Blog API Automation Results");
		sparkReporter.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.STANDARD);

		extentReports = new ExtentReports();
		extentReports.attachReporter(sparkReporter);

		// Get environment dynamically from system property, default to "test"
		String environment = System.getProperty("test.environment", "test");
		extentReports.setSystemInfo("Environment", environment.toUpperCase());
		extentReports.setSystemInfo("User", "QA Automation");
		extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
		extentReports.setSystemInfo("Test Framework", "TestNG + REST Assured");
	}

	@Override
	public void onTestStart(ITestResult result) {
		extentTest = extentReports.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
		extentTest.assignCategory(result.getTestClass().getName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		extentTest.log(Status.PASS, "Test passed successfully");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		extentTest.log(Status.FAIL, "Test failed: " + result.getThrowable().getMessage());
		extentTest.log(Status.FAIL, result.getThrowable());
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		extentTest.log(Status.SKIP, "Test skipped: " + result.getThrowable().getMessage());
	}

	@Override
	public void onFinish(ISuite suite) {
		if (extentReports != null) {
			extentReports.flush();
		}
	}
}