import lombok.extern.java.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * @author speed
 */
@Log
public class BidftaWebDriver {

    private final WebDriver driver;

    /**
     * New Bidfta Driver
     */
    public BidftaWebDriver() {
        // Could add check for preferred browser?
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-private");
        this.driver = new FirefoxDriver(options);

    }

    public void drive() {
        driver.get(Constants.BIDFTA_URL);
        // Login with creds provided in prefs.txt
        boolean loggedIn = login();

        if (!loggedIn) {
            // error message
            System.exit(0);
        }

    }

    /**
     * Attempts to log into BidFta.com with given credentials
     *
     * @return true if successfully logged in, false otherwise
     */
    private boolean login() {
        // click on the Login button
        boolean loggedIn = false;
        int loginAttempts = 0;
        waitBid(2);
        WebElement element = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[1]/div[2]/div/button"));
        if (element == null) {
            log.warning("Did not find button");
            return loggedIn;
        }
        element.click();
        waitBid(2);
        while (loginAttempts < 4) {
            //Username
            element = driver.findElement(By.id("username"));
            element.clear();
            log.info(Preferences.getUserName());
            element.sendKeys(Preferences.getUserName());


            //Password
            waitBid(2);
            element = driver.findElement(By.id("password"));
            element.clear();
            log.info(Preferences.getPassword());
            element.sendKeys(Preferences.getPassword());

            //Enter btn
            waitBid(3);
            element.sendKeys(Keys.ENTER);
            log.info(element.getTagName());
            element.click();
            waitBid(5);
            if (Constants.BIDFTA_URL.equals(this.driver.getCurrentUrl())) {
                loggedIn = true;
                break;
            }

            loginAttempts++;
        }

        return loggedIn;

    }

    /**
     * Helper method that tells the driver to wait for a desired amount of seconds
     *
     * @param seconds - Amount of seconds you want to wait
     */
    private void waitBid(double seconds) {
        long timeMillis = (long) (seconds * 1000);

        synchronized (this.driver) {
            try {
                driver.wait(timeMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}
