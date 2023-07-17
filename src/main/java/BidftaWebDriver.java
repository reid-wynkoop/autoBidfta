import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * @author speed
 */
@Slf4j
public class BidftaWebDriver {

    @Getter
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

        //Auctions
        openAuctions();

    }

    private void openAuctions() {
        WebElement auctionDivWE =
                this.driver.findElement(
                        By.xpath("/html/body/div[1]/div[4]/main/div/div/div/div[2]"));

        List<WebElement> auctionsList = auctionDivWE.findElements(By.xpath("div/a"));
        Utils.waitBid(3, this.driver);

        Queue<Auction> auctionsQ = Utils.createQueueOfAuctions(auctionsList);

        while (!auctionsQ.isEmpty()) {
            Utils.waitBid(2, this.driver);
            log.info("" + auctionsQ.size());


            //Gets top of queue, but does not remove
            Auction a = auctionsQ.peek();
            a.openAuction(this.driver);
            auctionsQ.remove();

        }


        //open in new tab
//        this.driver.executeScript("window.open('" + auctionsList.get(0).getAttribute("href") + "');");
//
//        Set<String> tabs = this.driver.getWindowHandles();
//        log.info("" + auctionsList.get(0).getAttribute("href"));
//        for (String tab : tabs) {
//            if (!tab.equals(this.driver.getWindowHandle())) {
//                this.driver.switchTo().window(tab);
//            }
//        }
//        //Need to wait bc the url changes
//        Utils.waitBid(3);
//        this.driver.get(Utils.createFilteredAuctionUrl(this.driver.getCurrentUrl()));
    }

    /**
     * Navigates to auctions with the given Zip Code and Distance parameters
     */
    private void navigateToAuctions() {
        Utils.waitBid(4, this.driver);

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
        Utils.waitBid(2, this.driver);

        //Sometimes there is a Banner hiding the Login Btn. Close it if exists.
        WebElement element = driver.findElement(By.xpath("/html/body/div[2]/div/div/div/div[1]/button"));
        if (element != null) {
            log.info("Clicking banner X button");
            element.click();

        }
        else {
            log.info("Banner button did not exist");
        }
        Utils.waitBid(2, this.driver);
        element = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[1]/div[2]/div/button"));
        if (element == null) {
            log.warn("Did not find button");
            return;
        }
        element.click();
        Utils.waitBid(2, this.driver);
        while (loginAttempts < 4) {
            log.info("Starting to login");
            //Username
            element = driver.findElement(By.id("username"));
            element.clear();
            log.info(Preferences.getUserName());
            element.sendKeys(Preferences.getUserName());

            //Password
            Utils.waitBid(2, this.driver);
            element = driver.findElement(By.id("password"));
            element.clear();
            log.info(Preferences.getPassword());
            element.sendKeys(Preferences.getPassword());

            //Enter btn
            Utils.waitBid(3, this.driver);
            element.sendKeys(Keys.ENTER);
            log.info(element.getTagName());
            element.click();
            Utils.waitBid(5, this.driver);
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


}
