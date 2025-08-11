package Academy;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class BrowserTest {

    @Test
    public void getData() {
        WebDriver driver = new ChromeDriver();
        try {
            driver.get("https://learnautomation.azurewebsites.net/webapp/");
            String text = driver.findElement(By.cssSelector("h1")).getText();
            System.out.println(text);
            Assert.assertTrue(text.equalsIgnoreCase("my devops Learing"));
        } finally {
            driver.quit();
        }
    }
}
