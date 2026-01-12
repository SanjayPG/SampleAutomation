package Academy;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class BrowserTest {

    private static final int TIMEOUT_SECONDS = 10;
    private static final Logger logger = LoggerFactory.getLogger(BrowserTest.class);

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testGoogleSearch() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // For CI uncomment headless:
        // options.addArguments("--headless");

        WebDriver driver = new ChromeDriver(options);

        try {
            logger.info("[{}] Navigating to Google", testName.getMethodName());
            driver.get("https://www.google.com/");
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_SECONDS);

            handleConsentDialog(wait);

            performSearch(wait, "Selenium");

            String title = driver.getTitle();
            logger.info("[{}] Page title after search: {}", testName.getMethodName(), title);
            Assert.assertTrue("Title should contain 'Selenium'", title.toLowerCase().contains("selenium"));

        } catch (Exception e) {
            logger.error("[{}] Unexpected error during test execution", testName.getMethodName(), e);
            throw e;
        } finally {
            takeScreenshot(driver, testName.getMethodName());
            driver.quit();
        }
    }

    @Test
    public void testGoogleSearch_WrongTitle() {
        // Real search but intentionally wrong title assertion to force a failure
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        try {
            logger.info("[{}] Navigating to Google (intentional wrong title test)", testName.getMethodName());
            driver.get("https://www.google.com/");
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_SECONDS);

            handleConsentDialog(wait);

            performSearch(wait, "Selenium");

            String title = driver.getTitle();
            logger.info("[{}] Page title after search: {}", testName.getMethodName(), title);

            // Intentionally assert a wrong title to fail the test
            Assert.assertTrue("Intentional failure: expecting wrong title", title.toLowerCase().contains("this_title_is_wrong"));

        } catch (AssertionError ae) {
            logger.warn("[{}] Intentional assertion failure occurred: {}", testName.getMethodName(), ae.getMessage());
            throw ae;
        } catch (Exception e) {
            logger.error("[{}] Unexpected error during test execution", testName.getMethodName(), e);
            throw e;
        } finally {
            takeScreenshot(driver, testName.getMethodName());
            driver.quit();
        }
    }

    @Test
    public void testGoogleSearch_NextPage_WrongName() {
        // Navigate to next results page then assert intentionally wrong page name
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(options);

        try {
            logger.info("[{}] Navigating to Google (intentional next page name mismatch)", testName.getMethodName());
            driver.get("https://www.google.com/");
            WebDriverWait wait = new WebDriverWait(driver, TIMEOUT_SECONDS);

            handleConsentDialog(wait);

            performSearch(wait, "Selenium");

            // Try to click the Next button (pagination)
            try {
                WebElement next = wait.until(ExpectedConditions.elementToBeClickable(By.id("pnnext")));
                next.click();
                logger.info("[{}] Clicked 'Next' to navigate to page 2", testName.getMethodName());
                wait.until(ExpectedConditions.titleContains("Selenium"));
            } catch (TimeoutException te) {
                logger.warn("[{}] Could not find or click 'Next' button - will continue to intentional assertion", testName.getMethodName());
            }

            String title = driver.getTitle();
            logger.info("[{}] Page title after attempting next: {}", testName.getMethodName(), title);

            // Intentionally assert wrong page name to fail
            Assert.assertTrue("Intentional failure: expecting wrong next page name", title.toLowerCase().contains("nonexistent_page_name"));

        } catch (AssertionError ae) {
            logger.warn("[{}] Intentional assertion failure occurred: {}", testName.getMethodName(), ae.getMessage());
            throw ae;
        } catch (Exception e) {
            logger.error("[{}] Unexpected error during test execution", testName.getMethodName(), e);
            throw e;
        } finally {
            takeScreenshot(driver, testName.getMethodName());
            driver.quit();
        }
    }

    private void handleConsentDialog(WebDriverWait wait) {
        try {
            WebElement consentButton = null;

            String[] buttonSelectors = {
                "L2AGLb",
                "W0wltc",
                "button[jsname='V67aGc']"
            };

            for (String selector : buttonSelectors) {
                try {
                    if (selector.startsWith("button[")) {
                        consentButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
                    } else {
                        consentButton = wait.until(ExpectedConditions.elementToBeClickable(By.id(selector)));
                    }
                    break;
                } catch (TimeoutException e) {
                    continue;
                }
            }

            if (consentButton != null) {
                consentButton.click();
                logger.info("Consent dialog accepted");
            }

        } catch (TimeoutException e) {
            logger.debug("No consent dialog found or timeout occurred - continuing with test");
        }
    }

    private void performSearch(WebDriverWait wait, String searchTerm) {
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
        searchBox.clear();
        searchBox.sendKeys(searchTerm);
        searchBox.sendKeys(Keys.RETURN);
        logger.info("Search performed for: {}", searchTerm);
    }

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
