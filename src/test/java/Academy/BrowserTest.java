package Academy;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class BrowserTest {

    @Test
    public void testGoogleSearch() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://www.google.com/");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Accept Google's consent dialog if present
            try {
                WebElement agreeButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//div[contains(@class, 'VfPpkd-RLmnJb') or @id='L2AGLb' or .='I agree' or .='Accept all']")));
                agreeButton.click();
            } catch (Exception e) {
                // If the dialog does not appear, continue
            }

            // Now wait for the search box to be visible and interactable
            WebElement searchBox = wait.until(ExpectedConditions.elementToBeClickable(By.name("q")));
            searchBox.sendKeys("Selenium");
            searchBox.sendKeys(Keys.RETURN);

            // Wait for the results page and check the title
            wait.until(ExpectedConditions.titleContains("Selenium"));
            String title = driver.getTitle();
            System.out.println("Page title is: " + title);
            Assert.assertTrue("Title should contain 'Selenium'", title.contains("Selenium"));
        } finally {
            driver.quit();
        }
    }
}
