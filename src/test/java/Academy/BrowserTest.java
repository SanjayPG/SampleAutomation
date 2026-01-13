//package Academy;
//
//import org.testng.Assert;
//import org.testng.ITestResult;
//import org.testng.annotations.AfterMethod;
//import org.testng.annotations.BeforeMethod;
//import org.testng.annotations.Test;
//import org.openqa.selenium.By;
//import org.openqa.selenium.Keys;
//import org.openqa.selenium.OutputType;
//import org.openqa.selenium.TakesScreenshot;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.chrome.ChromeDriver;
//import org.openqa.selenium.chrome.ChromeOptions;
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//import org.openqa.selenium.TimeoutException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//
//public class BrowserTest {
//
//    private static final int TIMEOUT_SECONDS = 10;
//    private static final Logger logger = LoggerFactory.getLogger(BrowserTest.class);
//    private String testMethodName;
//    private WebDriver driver;
//
//    @BeforeMethod
//    public void setUp(ITestResult result) {
//        testMethodName = result.getMethod().getMethodName();
//        WebDriverManager.chromedriver().setup();
//
//        ChromeOptions options = new ChromeOptions();
//        options.addArguments("--disable-blink-features=AutomationControlled");
//        options.addArguments("--no-sandbox");
//        options.addArguments("--disable-dev-shm-usage");
//        // For CI uncomment headless:
//        // options.addArguments("--headless");
//
//        driver = new ChromeDriver(options);
//    }
//
//    @AfterMethod
//    public void tearDown(ITestResult result) {
//        testMethodName = result.getMethod().getMethodName();
//        takeScreenshot(driver, testMethodName);
//        if (driver != null) {
//            driver.quit();
//        }
//    }
//
//    @Test
//    public void testGoogleSearch() {
//        try {
//            logger.info("[{}] Navigating to Google", testMethodName);
//            driver.get("https://www.google.com/");
//            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_SECONDS);
//
//            handleConsentDialog(wait);
//
//            performSearch(wait, "Selenium");
//
//            // Find and click any link containing "selenium"
//            try {
//                WebElement seleniumLink = wait.until(ExpectedConditions.presenceOfElementLocated(
//                        By.xpath("//a[contains(translate(., 'SELENIUM', 'selenium'), 'selenium')]")));
//                String linkText = seleniumLink.getText();
//                logger.info("[{}] Found link containing 'selenium': {}", testMethodName, linkText);
//
//                // Scroll into view and click using JavaScript to avoid interception issues
//                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
//                        seleniumLink);
//                Thread.sleep(500); // Brief pause after scroll
//                ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", seleniumLink);
//                logger.info("[{}] Successfully clicked on selenium link", testMethodName);
//            } catch (TimeoutException te) {
//                logger.warn("[{}] Could not find a link containing 'selenium'", testMethodName);
//            } catch (InterruptedException ie) {
//                Thread.currentThread().interrupt();
//                logger.warn("[{}] Thread interrupted during wait", testMethodName);
//            }
//
//        } catch (Exception e) {
//            logger.error("[{}] Unexpected error during test execution", testMethodName, e);
//            Assert.fail("Test failed with exception: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testGoogleSearch_WrongTitle() {
//        // Real search but intentionally wrong title assertion to force a failure
//        try {
//            logger.info("[{}] Navigating to Google (intentional wrong title test)", testMethodName);
//            driver.get("https://www.google.com/");
//            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_SECONDS);
//
//            handleConsentDialog(wait);
//
//            performSearch(wait, "Selenium");
//
//            String title = driver.getTitle();
//            logger.info("[{}] Page title after search: {}", testMethodName, title);
//
//            // Intentionally assert a wrong title to fail the test
//            Assert.assertTrue(title.toLowerCase().contains("this_title_is_wrong"),
//                    "Intentional failure: expecting wrong title");
//
//        } catch (AssertionError ae) {
//            logger.warn("[{}] Intentional assertion failure occurred: {}", testMethodName, ae.getMessage());
//            throw ae;
//        } catch (Exception e) {
//            logger.error("[{}] Unexpected error during test execution", testMethodName, e);
//            Assert.fail("Test failed with exception: " + e.getMessage());
//        }
//    }
//
//    @Test
//    public void testGoogleSearch_NextPage_WrongName() {
//        // Navigate to next results page then assert intentionally wrong page name
//        try {
//            logger.info("[{}] Navigating to Google (intentional next page name mismatch)", testMethodName);
//            driver.get("https://www.google.com/");
//            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_SECONDS);
//
//            handleConsentDialog(wait);
//
//            performSearch(wait, "Selenium");
//
//            // Try to click the Next button (pagination)
//            try {
//                WebElement next = wait.until(ExpectedConditions.elementToBeClickable(By.id("pnnext")));
//                next.click();
//                logger.info("[{}] Clicked 'Next' to navigate to page 2", testMethodName);
//                wait.until(ExpectedConditions.titleContains("Selenium"));
//            } catch (TimeoutException te) {
//                logger.warn("[{}] Could not find or click 'Next' button - will continue to intentional assertion",
//                        testMethodName);
//            }
//
//            String title = driver.getTitle();
//            logger.info("[{}] Page title after attempting next: {}", testMethodName, title);
//
//            // Intentionally assert wrong page name to fail
//            Assert.assertTrue(title.toLowerCase().contains("nonexistent_page_name"),
//                    "Intentional failure: expecting wrong next page name");
//
//        } catch (AssertionError ae) {
//            logger.warn("[{}] Intentional assertion failure occurred: {}", testMethodName, ae.getMessage());
//            throw ae;
//        } catch (Exception e) {
//            logger.error("[{}] Unexpected error during test execution", testMethodName, e);
//            Assert.fail("Test failed with exception: " + e.getMessage());
//        }
//    }
//
//    private void handleConsentDialog(WebDriverWait wait) {
//        try {
//            WebElement consentButton = null;
//
//            String[] buttonSelectors = {
//                    "L2AGLb",
//                    "W0wltc",
//                    "button[jsname='V67aGc']"
//            };
//
//            for (String selector : buttonSelectors) {
//                try {
//                    if (selector.startsWith("button[")) {
//                        consentButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
//                    } else {
//                        consentButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(selector)));
//                    }
//                    break;
//                } catch (TimeoutException e) {
//                    continue;
//                }
//            }
//
//            if (consentButton != null) {
//                consentButton.click();
//                logger.info("Consent dialog accepted");
//            }
//
//        } catch (TimeoutException e) {
//            logger.debug("No consent dialog found or timeout occurred - continuing with test");
//        }
//    }
//
//    private void performSearch(WebDriverWait wait, String searchTerm) {
//        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
//        searchBox.clear();
//        searchBox.sendKeys(searchTerm);
//        searchBox.sendKeys(Keys.RETURN);
//        logger.info("Search performed for: {}", searchTerm);
//    }
//
//    private void takeScreenshot(WebDriver driver, String name) {
//        if (driver == null) {
//            logger.debug("Driver is null, skipping screenshot for {}", name);
//            return;
//        }
//
//        try {
//            Path screenshotsDir = Paths.get("target", "test-logs", "screenshots");
//            Files.createDirectories(screenshotsDir);
//
//            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//            Path dest = screenshotsDir.resolve(name + ".png");
//            Files.copy(srcFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
//            logger.info("Saved screenshot for {} to {}", name, dest.toString());
//        } catch (IOException ioe) {
//            logger.error("Failed to save screenshot for {}", name, ioe);
//        } catch (Exception e) {
//            logger.error("Unexpected error while taking screenshot for {}", name, e);
//        }
//    }
//}
