import jdk.jshell.execution.Util;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Representation of a BidFTA auction.
 * Data will be stored in this class and output to a file.
 */
@Slf4j
public class Auction {

    @Getter
    private String location;
    @Getter
    private String endDate;
    @Getter
    private List<String> pickupDates;
    @Getter

    private List<Item> items;
    @Getter
    private String auctionName;

    private String href;
    private FirefoxDriver driver;


    private int amtPages;
    private int totalItems;

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
        this.pickupDates = new ArrayList<>();
        this.items = new ArrayList<>();
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
        //Navigate to last one opened
        this.driver.switchTo().window(tabs.get(tabs.size() - 1));
        getMetaData();


    }

    /**
     * Gets the metaData: Auction name, location, end date, etc. for the auction
     */
    private void getMetaData() {

        this.auctionName = this.driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/main/div/div[1]/aside/div/div/div[2]/a")).getText();
        log.warn("Auction Name:" + this.auctionName);
        this.endDate = this.driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/main/div/div[1]/aside/div/div/div[4]/div[1]/p")).getText();

        this.location = this.driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/main/div/div[1]/aside/div/div/div[3]/p[1]")).getText();

        WebElement datesParent = this.driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/main/div/div[1]/aside/div/div/div[5]"));
        for (WebElement en : datesParent.findElements(By.className("py-1"))) {
            this.pickupDates.add(en.getText());
        }
        WebElement pages = this.driver.findElement(By.xpath("/html/body/div[1]/div/div[4]/main/div/div[1]/div/div[3]/div[2]/div/p[2]"));

        String[] txt = pages.getText().split(" ");
        this.totalItems = Integer.parseInt(txt[txt.length - 1]);
        this.amtPages = (int) Math.ceil((double) totalItems / 24);
    }

    /**
     * Navigates through the auction, finding items that match the user specified criteria
     */
    public void driveAuction() {

        int amtOnPage = 24;
        String url = this.driver.getCurrentUrl();
        for (int i = 1; i <= this.amtPages; i++) {
            String filteredURL = url.substring(0, url.length() - 1) + i;
            filteredURL += Preferences.getItemStatusUrlFilter();
            this.driver.navigate().to(filteredURL);
            log.warn(this.driver.getCurrentUrl());
            String link;

            //On the last page, find the amount of Items
            if (i == this.amtPages) {
                amtOnPage = this.totalItems % amtOnPage;
            }

            for (int j = 1; j <= amtOnPage; j++) {
                try {

                    link = this.driver.findElement(By.xpath(
                            "/html/body/div[1]/div/div[4]/main/div/div[1]/div/div[3]/div[2]/div/div/div/div[" + j + "]/div/div/div[1]/div/div[1]/div[1]/a")).getAttribute("href");
                    this.driver.navigate().to(link);
                    Utils.waitBid(2, this.driver);
                    checkMetaData();

                    //Go Back to the auction
                    this.driver.navigate().back();

                    Utils.waitBid(1, this.driver);
                } catch (Exception e) {
//                    log.error(e.getMessage());
                }
            }

        }
        //close the currently open tab
        this.driver.close();

        //Write AuctionName to file
        try {
            String appendLine = auctionName + "\n";
            //Add Auction Name to file
            Files.write(Paths.get("src/main/resources/Auctions.txt"),
                    appendLine.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("Error while writing Auction name to file");
        }

        //Write all Items to file
        try {
            for (Item i : getItems()) {
                String appendLine = i.toString() + "\n";
                //Add Auction Name to file
                Files.write(Paths.get("src/main/resources/Items.txt"),
                        appendLine.getBytes(), StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            log.error("Error while writing Auction name to file");
        }
    }

    /**
     * Checks the given Auction Item against the user specified criteria.
     * If it passes, add to list for output
     */
    private void checkMetaData() {
        Map<String, String> tblData = getDataFromTable();
        //Check MinMSRP
        String msrpStr = tblData.get("MSRP");
        double msrpDbl = getDblFromPrice(msrpStr);
        //The MSRP is lower that your minimum MSRP
        if (msrpDbl < Preferences.getMinMSRP()) {
            log.info("MSRP: " + msrpStr + "  Skipped");
            return;
        }
        log.info("MSRP: " + msrpStr);
        String curBid = this.driver.findElement(By.xpath("/html/body/div[1]/div[4]/main/div/div[1]/div/div[3]/div[1]/div/div/div[1]/div[2]/span")).getText();
        double curBidDbl = getDblFromPrice(curBid);

        //The current bid is MORE than what your max bid is
        if (curBidDbl > Preferences.getMaxBid()) {
            log.info("CurBid: " + curBid + "  Skipped");
            return;
        }
        log.info("CurBid: " + curBid);

        String condition = tblData.getOrDefault("Condition", "N/A");

        //If the items condition is NOT in your specified condition(s)
//        if (!Preferences.getItemStatus().contains(condition)) {
//            return;
//        }
        String title = tblData.getOrDefault("Title", "N/A").toUpperCase();
        String description = tblData.getOrDefault("Description", "N/A").toUpperCase();

        //If the title or Description contains any specified Excluded words, skip item
        for (String word : Preferences.getExcludedWords()) {
            String upr = word.toUpperCase();
            if (title.contains(upr) || description.contains(upr)) {
                log.info("'" + word + "' was found. Skipped");
                return;
            }
        }

        String additionalInfo = tblData.getOrDefault("Additional Info", "N/A");

        String pickupLocation = tblData.getOrDefault("Pickup Location", "N/A");
        String lotCode = tblData.getOrDefault("Lot Code", "N/A");

        //All checks Pass
        //Use Title and Description for 'Exclude' check
        Item item = new Item(title, msrpDbl, condition, description, additionalInfo, curBidDbl, pickupLocation, lotCode, this.driver.getCurrentUrl());


        log.warn("Item added!!!");
        log.warn(item.toString());
        this.items.add(item);


    }

    private @NotNull Map<String, String> getDataFromTable() {
        Map<String, String> retMap = new HashMap<>();
        WebElement table = this.driver.findElement(By.xpath("/html/body/div[1]/div[4]/main/div/div[1]/div/div[3]/div[5]/table[@class = 'max-w-3xl']"));
        List<WebElement> rows = table.findElements(By.xpath(".//tr"));
        for (WebElement row : rows) {
            String key = row.findElement(By.xpath(".//td[1]")).getText();
            String value = row.findElement(By.xpath(".//td[2]")).getText();
            if ("MSRP\n!".equals(key)) {
                //MSRP has a "!" in td2
                retMap.put("MSRP", value);
            }
            else {
                retMap.put(key, value);
            }
        }
        return retMap;
    }

    private double getDblFromPrice(@NotNull String price) {
        //Remove '$'
        String tmp = price.substring(1);
        return Double.parseDouble(tmp);
    }
}
