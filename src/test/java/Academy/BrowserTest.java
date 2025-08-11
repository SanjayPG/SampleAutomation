package Academy;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class GoogleSearchTest {

    @Test
    public void testGoogleSearch() {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://www.google.com/");
            WebElement searchBox = driver.findElement(By.name("q"));
            searchBox.sendKeys("Selenium");
            searchBox.sendKeys(Keys.RETURN); // Press Enter

            // Wait for results page to load (simple sleep for demo; use WebDriverWait in real tests)
            Thread.sleep(2000);

            String title = driver.getTitle();
            System.out.println("Page title is: " + title);
            Assert.assertTrue("Title should contain 'Selenium'", title.contains("Selenium"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
