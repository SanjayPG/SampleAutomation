package Academy;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unit test for simple App.
 */
public class AppTest {
    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);

    /**
     * Rigourous Test :-)
     */
    @Test
    public void testApp() {
        logger.info("Starting testApp");
        Assert.assertTrue(true);
        logger.info("Finished testApp");
    }
}
