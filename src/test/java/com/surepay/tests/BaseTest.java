package com.surepay.tests;

import org.testng.ITestResult;
import org.testng.annotations.*;
import com.surepay.framework.services.*;
import com.surepay.framework.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;

@Slf4j
public abstract class BaseTest {
    protected UserService userService;

    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        log.info("=== Starting SurePay Blog API Test Suite ===");
        ConfigManager.getInstance();
        log.info("Test suite setup completed successfully");
    }
    
    @BeforeMethod(alwaysRun = true)
    public void setupTest(Method method) {
        userService = new UserService();
        String testName = method.getName();
        log.info("Starting test: {}", testName);
        performHealthChecks();
    }
    
    private void performHealthChecks() {
        boolean userServiceHealthy = userService.isServiceHealthy();
        
        if (!userServiceHealthy) {
            throw new RuntimeException("Service health check failed - cannot proceed with tests");
        }
    }
    
    @AfterMethod(alwaysRun = true)
    public void teardownTest(ITestResult result) {
        log.info("Test completed: {} - Status: {}", result.getName(), result.getStatus());
    }

    @AfterSuite(alwaysRun = true)
    public void teardownSuite() {
        log.info("=== Test Suite Completed ===");
    }
}