package Academy;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.TimeoutException;

import java.time.Duration;

public class BrowserTest {

    private static final int TIMEOUT_SECONDS = 10;

    @Test
    public void testGoogleSearch() {
        WebDriverManager.chromedriver().setup();

        // Configure Chrome options for better stability
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        // Uncomment the next line for headless mode
        // options.addArguments("--headless");

        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://www.google.com/");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(TIMEOUT_SECONDS));

            // Handle Google's consent dialog with more specific selectors
            handleConsentDialog(wait);

            // Search for "Selenium"
            performSearch(wait, "Selenium");

            // Verify results
            verifySearchResults(driver, wait, "Selenium");

        } finally {
            driver.quit();
        }
    }

    private void handleConsentDialog(WebDriverWait wait) {
        try {
            // Try multiple common selectors for consent dialog
            WebElement consentButton = null;

            // Common button IDs and text patterns for Google consent
            String[] buttonSelectors = {
                "L2AGLb",           // Common ID for "I agree" button
                "W0wltc",           // Alternative ID
                "button[jsname='V67aGc']" // CSS selector approach
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
                    // Try next selector
                    continue;
                }
            }

            if (consentButton != null) {
                consentButton.click();
                System.out.println("Consent dialog accepted");
            }

        } catch (TimeoutException e) {
            System.out.println("No consent dialog found or timeout occurred - continuing with test");
        }
    }

    private void performSearch(WebDriverWait wait, String searchTerm) {
        // Wait for search box and perform search
        WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
        searchBox.clear(); // Clear any existing text
        searchBox.sendKeys(searchTerm);
        searchBox.sendKeys(Keys.RETURN);
        System.out.println("Search performed for: " + searchTerm);
    }

    private void verifySearchResults(WebDriver driver, WebDriverWait wait, String expectedTerm) {
        // Wait for results page to load
        wait.until(ExpectedConditions.titleContains(expectedTerm));

        String title = driver.getTitle();
        System.out.println("Page title is: " + title);

        // More comprehensive assertion
        Assert.assertTrue(
            String.format("Title should contain '%s', but was: '%s'", expectedTerm, title),
            title.toLowerCase().contains(expectedTerm.toLowerCase())
        );

        // Additional verification - check if results are present
        try {
            WebElement resultsStats = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("result-stats"))
            );
            System.out.println("Search results found: " + resultsStats.getText());
        } catch (TimeoutException e) {
            System.out.println("Could not find results stats, but title verification passed");
        }
    }
}
