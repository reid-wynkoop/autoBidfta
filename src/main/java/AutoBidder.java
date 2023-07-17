import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author speed
 */
@Slf4j
public class AutoBidder {

    @Getter
    private static List<String> auctionsAnalyzed;

    public static void main(String[] args) {

        auctionsAnalyzed = Utils.readInAuctions();
        // Read in the prefs.txt and initialize the Preferences
        Preferences.init();

        if (!Preferences.verifyPreferences()) {
            log.error("There were errors with the given preferences. Cannot continue with AutoBidder");
            System.exit(150);
        }

        BidftaWebDriver driver = new BidftaWebDriver();
        driver.drive();


    }

}
