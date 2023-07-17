import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a BidFTA auction.
 * Data will be stored in this class and output to a file.
 */
@Slf4j
public class Auction {

    private String location;
    private String endDate;
    private List<String> pickupDates;

    private List<Item> items;

    private String href;
    private FirefoxDriver driver;


    private int amtPages;

    public Auction() {
//        Item i = Item.builder().build();
    }

    /**
     * Used as the initial constructor
     *
     * @param we - WebElement - aka image/link to the auction.
     */
    public Auction(@NotNull WebElement we) {
        this.href = we.getAttribute("href");
    }

    /**
     * Opens the auction in a new tab.
     * From there, we analyze the items.
     */
    public void openAuction(FirefoxDriver driver) {
        this.driver = driver;

        //open in new tab
        this.driver.executeScript("window.open('" + this.href + "');");
        Utils.waitBid(2, this.driver);
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        this.driver.switchTo().window(tabs.get(1));
        getMetaData();


    }

    /**
     * Gets the metaData: Auction name, location, end date, etc. for the auction
     */
    private void getMetaData() {

        log.warn(this.driver.findElement(By.xpath(Constants.AUCTION_BID_XPATH)).getText());
    }
}
