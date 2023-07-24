import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    private final String dateStr;

    /**
     * New Bidfta Driver
     */
    public BidftaWebDriver() {
        // Could add check for preferred browser?
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-private");
        this.driver = new FirefoxDriver(options);
        dateStr = new SimpleDateFormat("MM/dd/yyyy").format(new Date());

    }

    public void drive() {
        driver.get(Constants.BIDFTA_URL);

        // Login with creds provided in prefs.txt
        login();
        //Navigate the Auctions
        navigateToAuctions();

        //Auctions
        openAuctions();

        //Close browser
        this.driver.quit();
    }

    private void openAuctions() {
        WebElement auctionDivWE =
                this.driver.findElement(
                        By.xpath("/html/body/div[1]/div[4]/main/div/div/div/div[2]"));

        Utils.waitBid(2, this.driver);
        List<WebElement> auctionsList = auctionDivWE.findElements(By.xpath("div/a"));
        Utils.waitBid(2, this.driver);

        //Write all Items to file
        try {
                String appendLine = " ----------------- " + dateStr + "-----------------\n";
                //Add Auction Name to file
                Files.write(Paths.get("src/main/resources/Items.txt"),
                        appendLine.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Error while writing Auction name to file");
        }

        Queue<Auction> auctionsQ = Utils.createQueueOfAuctions(auctionsList);

        while (!auctionsQ.isEmpty()) {
            Utils.waitBid(1, this.driver);
            log.info("Auctions Left to search " + auctionsQ.size());


            //Gets top of queue, but does not remove
            Auction a = auctionsQ.peek();
            a.openAuction(this.driver);

            if (!Preferences.getPreviousAuctions().contains(a.getAuctionName())) {
                a.driveAuction();
            }
            else {
                log.warn("Auction skipped: " + a.getAuctionName());
                this.driver.close();
            }
            Set<String> handles = this.driver.getWindowHandles();
            this.driver.switchTo().window(handles.iterator().next());

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
        Utils.waitBid(1, this.driver);

        // "https://www.bidfta.com/location?miles=10&zipCode=45420"
        String url = "https://www.bidfta.com/location?miles=" + Preferences.getBidZipDistance() + "&zipCode=" + Preferences.getBidZipCode();

        driver.get(url);

    }

    /**
     * Attempts to log into BidFta.com with given credentials
     */
    private void login() {
        // click on the Login button
        int loginAttempts = 0;
        Utils.waitBid(1, this.driver);

        //Sometimes there is a Banner hiding the Login Btn. Close it if exists.
        //TODO - fix banner check
        WebElement element;
//        = driver.findElement(By.xpath("/html/body/div[2]/div/div/div/div[1]/button"));
//        if (element != null) {
//            log.info("Clicking banner X button");
//            element.click();
//
//        }
//        else {
//            log.info("Banner button did not exist");
//        }
        Utils.waitBid(1, this.driver);
        element = driver.findElement(By.xpath("/html/body/div[1]/div[1]/div[1]/div[2]/div/button"));
        if (element == null) {
            log.warn("Did not find button");
            return;
        }
        element.click();
        Utils.waitBid(1, this.driver);
        while (loginAttempts < 4) {
            log.info("Starting to login");
            //Username
            element = driver.findElement(By.id("username"));
            element.clear();
            log.info("UserName: "+Preferences.getUserName());
            element.sendKeys(Preferences.getUserName());

            //Password
            Utils.waitBid(1, this.driver);
            element = driver.findElement(By.id("password"));
            element.clear();
            log.info("Password: " + Preferences.getPassword());
            element.sendKeys(Preferences.getPassword());

            //Enter btn
            Utils.waitBid(1, this.driver);
            element.sendKeys(Keys.ENTER);
            element.click();
            Utils.waitBid(2, this.driver);
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
