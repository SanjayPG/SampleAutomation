package Academy;

import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SauceDemoTest {

    private static final int TIMEOUT_SECONDS = 10;
    private static final Logger logger = LoggerFactory.getLogger(SauceDemoTest.class);
    private static final String SAUCE_DEMO_URL = "https://www.saucedemo.com/";
    private static final String VALID_USERNAME = "standard_user";
    private static final String VALID_PASSWORD = "secret_sauce";

    private static final By USERNAME_FIELD = By.id("user-name");
    private static final By PASSWORD_FIELD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By INVENTORY_CONTAINER = By.id("inventory_container");

    private WebDriver driver;
    private WebDriverWait wait;
    private String testMethodName;

    @BeforeMethod
    public void setUp(ITestResult result) {
        testMethodName = result.getMethod().getMethodName();
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // For CI uncomment headless:
        // options.addArguments("--headless");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, TIMEOUT_SECONDS);
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        testMethodName = result.getMethod().getMethodName();
        takeScreenshot(driver, testMethodName);
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testSauceDemoLogin_Success() {
        try {
            logger.info("[{}] Starting positive login test with valid credentials", testMethodName);
            logger.info("[{}] Navigating to SauceDemo", testMethodName);
            driver.get(SAUCE_DEMO_URL);

            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(USERNAME_FIELD));
            usernameField.clear();
            usernameField.sendKeys(VALID_USERNAME);
            logger.info("[{}] Entered username: {}", testMethodName, VALID_USERNAME);

            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(PASSWORD_FIELD));
            passwordField.clear();
            passwordField.sendKeys(VALID_PASSWORD);
            logger.info("[{}] Entered password", testMethodName);

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON));
            loginButton.click();
            logger.info("[{}] Clicked login button", testMethodName);

            wait.until(ExpectedConditions.presenceOfElementLocated(INVENTORY_CONTAINER));
            logger.info("[{}] Successfully logged in - inventory page loaded", testMethodName);

            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("inventory.html"),
                    "Should be on inventory page after successful login");
            logger.info("[{}] Test PASSED: Login successful - URL: {}", testMethodName, currentUrl);
        } catch (Exception e) {
            logger.error("[{}] Test FAILED: Exception occurred during positive login test", testMethodName, e);
            Assert.fail("Login test failed with exception: " + e.getMessage());
        }
    }

    @Test
    public void testSauceDemoLoginWithValidUser_Success() {
        try {
            logger.info("[{}] Starting positive login test with valid credentials", testMethodName);
            logger.info("[{}] Navigating to SauceDemo", testMethodName);
            driver.get(SAUCE_DEMO_URL);

            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(USERNAME_FIELD));
            usernameField.clear();
            usernameField.sendKeys("Sanjay");
            logger.info("[{}] Entered username: {}", testMethodName, "Sanjay");

            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(PASSWORD_FIELD));
            passwordField.clear();
            passwordField.sendKeys("Password123");
            logger.info("[{}] Entered password", testMethodName);

            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON));
            loginButton.click();
            logger.info("[{}] Clicked login button", testMethodName);

            wait.until(ExpectedConditions.presenceOfElementLocated(INVENTORY_CONTAINER));
            logger.info("[{}] Successfully logged in - inventory page loaded", testMethodName);

            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("inventory.html"),
                    "Should be on inventory page after successful login");
            logger.info("[{}] Test PASSED: Login successful - URL: {}", testMethodName, currentUrl);
        } catch (Exception e) {
            logger.error("[{}] Login failed: Exception occurred during login, check the user id or password", testMethodName, e);

            // Try to capture error message from the application
            try {
                WebElement errorElement = driver.findElement(By.cssSelector("[data-test='error']"));
                String errorMessage = errorElement.getText();
                logger.error("[{}] Application Error Message: {}", testMethodName, errorMessage);
                Assert.fail("Login test failed - Application Error: " + errorMessage + " | Exception: " + e.getMessage());
            } catch (Exception errorCaptureException) {
                logger.warn("[{}] Could not capture error message from application", testMethodName);
                Assert.fail("Login test failed with exception: " + e.getMessage());
            }
        }
    }

//    @Test
//    public void testSauceDemoLogin_UserCreatedCredentials() {
//        try {
//            logger.info("[{}] Starting negative test with invalid credentials (username: Sanjay, password: password)", testMethodName);
//            logger.info("[{}] Navigating to SauceDemo", testMethodName);
//            driver.get(SAUCE_DEMO_URL);
//
//            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(USERNAME_FIELD));
//            usernameField.clear();
//            usernameField.sendKeys("Sanjay");
//            logger.info("[{}] Entered valid username: Sanjay", testMethodName);
//
//            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(PASSWORD_FIELD));
//            passwordField.clear();
//            passwordField.sendKeys("password");
//            logger.info("[{}] Entered valid password", testMethodName);
//
//            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON));
//            loginButton.click();
//            logger.info("[{}] Clicked login button", testMethodName);
//
//            // This should fail - we're using invalid credentials
//            try {
//                wait.until(ExpectedConditions.presenceOfElementLocated(INVENTORY_CONTAINER));
//                String currentUrl = driver.getCurrentUrl();
//                logger.error("[{}] Test FAILED: Login succeeded with invalid credentials (unexpected behavior) - URL: {}",
//                        testMethodName, currentUrl);
//                Assert.fail("Test failed: Login succeeded with invalid credentials when it should have failed");
//            } catch (org.openqa.selenium.TimeoutException te) {
//                // Expected behavior - login should fail with invalid credentials
//                logger.info("[{}] Test PASSED: Login correctly failed with invalid credentials (expected behavior)", testMethodName);
//
//                // Verify error message is displayed
//                try {
//                    WebElement errorMessage = driver.findElement(By.cssSelector("[data-test='error']"));
//                    String errorText = errorMessage.getText();
//                    logger.info("[{}] Error message displayed: {}", testMethodName, errorText);
//                    Assert.assertTrue(errorText.contains("Username and password do not match"),
//                            "Error message should indicate invalid credentials");
//                    logger.info("[{}] Test completed successfully - Invalid credentials were rejected as expected", testMethodName);
//                } catch (Exception e) {
//                    logger.warn("[{}] Could not verify error message: {}", testMethodName, e.getMessage());
//                }
//            }
//        } catch (Exception e) {
//            logger.error("[{}] Test FAILED: Unexpected exception occurred during login test", testMethodName, e);
//            Assert.fail("login test failed with unexpected exception: " + e.getMessage());
//        }
//    }

    private void takeScreenshot(WebDriver driver, String name) {
        if (driver == null) {
            logger.debug("Driver is null, skipping screenshot for {}", name);
            return;
        }

        try {
            Path screenshotsDir = Paths.get("target", "test-logs", "screenshots");
            Files.createDirectories(screenshotsDir);

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dest = screenshotsDir.resolve(name + ".png");
            Files.copy(srcFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            logger.info("Saved screenshot for {} to {}", name, dest.toString());
        } catch (IOException ioe) {
            logger.error("Failed to save screenshot for {}", name, ioe);
        } catch (Exception e) {
            logger.error("Unexpected error while taking screenshot for {}", name, e);
        }
    }
}
