import lombok.extern.java.Log;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.List;
import java.util.Set;

/**
 * @author speed
 */
@Log
public class BidftaWebDriver {

    private final FirefoxDriver driver;

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
        login();
        //Navigate the Auctions
        navigateToAuctions();

        //Auctions are
        openAuctions();

    }

    private void openAuctions() {
        WebElement auctions =
                this.driver.findElement(
                        By.xpath("/html/body/div[1]/div[4]/main/div/div/div/div[2]"));

        List<WebElement> auction = auctions.findElements(By.xpath("div/a"));
        waitBid(3);


        //open in new tab
        this.driver.executeScript("window.open('" + auction.get(0).getAttribute("href") + "');");

        Set<String> tabs = this.driver.getWindowHandles();
        log.info("" + auction.get(0).getAttribute("href"));
        for (String tab : tabs) {
            if (!tab.equals(this.driver.getWindowHandle())) {
                this.driver.switchTo().window(tab);
            }
        }
        //Need to wait bc the url changes
        waitBid(3);
        this.driver.get(Utils.createFilteredAuctionUrl(this.driver.getCurrentUrl()));
    }

    /**
     * Navigates to auctions with the given Zip Code and Distance parameters
     */
    private void navigateToAuctions() {
        waitBid(4);

        // "https://www.bidfta.com/location?miles=10&zipCode=45420"
        String url = "https://www.bidfta.com/location?miles=" + Preferences.getBidZipDistance() + "&zipCode=" + Preferences.getBidZipCode();

        driver.get(url);

    }

    /**
     * Attempts to log into BidFta.com with given credentials
     *
     * @return true if successfully logged in, false otherwise
     */
    private void login() {
        // click on the Login button
        int loginAttempts = 0;
        waitBid(2);

        //Sometimes there is a Banner hiding the Login Btn. Close it if exists.
        WebElement element = driver.findElement(By.xpath("/html/body/div[2]/div/div/div/div[1]/button"));
        if (element != null) {
            log.info("Clicking banner X button");
            element.click();

        }
        else {
            log.info("Banner button did not exist");
        }
        waitBid(2);
        element = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[1]/div[2]/div/button"));
        if (element == null) {
            log.warning("Did not find button");
            return;
        }
        element.click();
        waitBid(2);
        while (loginAttempts < 4) {
            log.info("Starting to login");
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
            log.info(this.driver.getCurrentUrl());
            //Login goes to dashboard
            if (Constants.BIDFTA_DASHBOARD_URL.equals(this.driver.getCurrentUrl())) {
                return;
            }

            loginAttempts++;
        }

        //Could not Log in
        System.exit(120);

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
