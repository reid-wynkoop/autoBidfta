import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.util.*;

/**
 * Utility Class
 */
@Slf4j
public final class Utils {
    private Utils() {
        throw new IllegalStateException("Utility class");
    }

    public static String createFilteredAuctionUrl(String href) {
        return href + Preferences.getItemStatusUrlFilter();
    }

    /**
     * Reads in the Auctions.txt file. We don't want to analyze an action multiple times.
     *
     * @return - A list of Auctions that we have already looked at
     */
    public static List<String> readInAuctions() {
        List<String> retList = new ArrayList<>();
        File file = new File("src/main/resources/Auctions.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);

                    if (!line.isBlank()) {
                        retList.add(line);
                    }
                }
            } catch (IOException e) {
                log.info("Error while reading Auctions.txt. Returning an empty list");
                return new ArrayList<>();
            }
        }
        else {
            log.info("Auction File cannot be found");
        }
        return retList;
    }

    /**
     * @param auction
     * @return
     */
    public static @NotNull Queue<Auction> createQueueOfAuctions(@NotNull List<WebElement> auction) {
        Queue<Auction> auctions = new LinkedList<>();

        auction.forEach(c -> auctions.add(new Auction(c)));

        return auctions;
    }

    /**
     * Helper method that tells the driver to wait for a desired amount of seconds
     *
     * @param seconds - Amount of seconds you want to wait
     */
    public static void waitBid(double seconds, final @NotNull FirefoxDriver driverf) {
        long timeMillis = (long) (seconds * 1000);

        synchronized (driverf) {
            try {
                driverf.wait(timeMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }
}
