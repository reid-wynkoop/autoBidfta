import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Stores local preferences like username/password, bidding location, max bid,
 * etc.
 *
 * @author speed
 */
@Slf4j
public class Preferences {

    private Preferences() {
        throw new IllegalStateException("Preference class");
    }

    @Getter
    private static String userName;
    @Getter
    private static String password;
    @Getter
    private static double maxBid;
    @Getter
    private static double minMSRP;
    @Getter
    private static List<String> itemStatus;
    @Getter
    private static String itemStatusUrlFilter;
    @Getter
    private static List<String> excludedWords;
    @Getter
    private static boolean autoBid;

    @Getter
    private static String bidZipCode;

    @Getter
    private static String bidZipDistance;

    @Getter
    private static List<String> previousAuctions;


    // Reads in the prefs.txt file and initialized the preferences
    public static void init() {

        File f = new File("src/main/resources/prefs.txt");
        if (f.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = reader.readLine()) != null) {
//                    log.info(line);
                    // Lines starting with '#' are comments.
                    if (!line.isBlank() && !line.startsWith("#")) {
                        Preferences.setPreference(line);
                    }
                }
//                Preferences.printOutPrefs();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            log.info("Preferences File does not exist");
        }

        previousAuctions = new ArrayList<>();
        f = new File("src/main/resources/Auctions.txt");
        if (f.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(f))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    previousAuctions.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * Take a line from the prefs.txt and sets the corresponding preference
     */
    public static void setPreference(String line) {
        if (line.startsWith(Constants.USERNAME)) {
            Preferences.userName = stripLine(line, Constants.USERNAME);
        }
        else if (line.startsWith(Constants.PASSWORD)) {
            Preferences.password = stripLine(line, Constants.PASSWORD);
        }
        else if (line.startsWith(Constants.MIN_MSRP)) {
            line = stripLine(line, Constants.MIN_MSRP);
            if (line.startsWith("$")) {
                Preferences.minMSRP = Double.parseDouble(line.substring(1));
            }
            else {
                Preferences.minMSRP = Double.parseDouble(line);
            }
        }
        else if (line.startsWith(Constants.MAX_BID)) {
            line = stripLine(line, Constants.MAX_BID);
            if (line.startsWith("$")) {
                Preferences.maxBid = Double.parseDouble(line.substring(1));
            }
            else if (line.endsWith("%")) {
                double percent = Double.parseDouble(line.substring(0, line.length() - 1));
                // i.e. if minMsrp is set
                if (!Double.isNaN(Preferences.minMSRP)) {
                    Preferences.maxBid = Preferences.minMSRP * (percent / 100);
                }
                else {
                    log.error("MinMSRP was null when assigning MaxBid. Move MinMSRP before MaxBid if prefs.txt");
                }
            }
        }
        else if (line.startsWith(Constants.BID_ZIP_CODE)) {
            Preferences.bidZipCode = stripLine(line, Constants.BID_ZIP_CODE);
        }
        else if (line.startsWith(Constants.BID_ZIP_DISTANCE)) {
            Preferences.bidZipDistance = stripLine(line, Constants.BID_ZIP_DISTANCE);
        }
        else if (line.startsWith(Constants.ITEM_STATUS)) {
            line = stripLine(line, Constants.ITEM_STATUS);
            Preferences.itemStatus = new ArrayList<>();
            List<String> tmp =  Arrays.asList(line.split(","));
            tmp.forEach(c -> Preferences.itemStatus.add(c.strip()));
            Preferences.itemStatusUrlFilter = createItemStatusUrlFilter();

        }
        else if (line.startsWith(Constants.EXCLUDE)) {
            line = stripLine(line, Constants.EXCLUDE);
            Preferences.excludedWords = Arrays.asList(line.split(","));
        }
        else if (line.startsWith(Constants.AUTO_BID)) {
            line = stripLine(line, Constants.AUTO_BID);
            Preferences.autoBid = "Y".equalsIgnoreCase(line) || "Yes".equalsIgnoreCase(line);
        }
    }

    private static String createItemStatusUrlFilter() {
        if (Preferences.getItemStatus().isEmpty()) {
            return "";
        }
        StringBuilder url = new StringBuilder("?itemConditionIds=");
        boolean first = true;
        //?itemConditionIds=1%2C3
        for (String status : Preferences.getItemStatus()) {
            log.info(status);
            switch (status.trim()) {
                case "Open Box" -> {
                    if (first) {
                        first = false;
                        url.append("1");
                    }
                    else {
                        url.append("%2C1");
                    }
                }
                case "Appears New" -> {
                    if (first) {
                        first = false;
                        url.append("3");
                    }
                    else {
                        url.append("%2C3");
                    }
                }
                case "Brand New" -> {
                    if (first) {
                        first = false;
                        url.append("5");
                    }
                    else {
                        url.append("%2C5");
                    }
                }
            }
        }

        return url.toString();
    }

    /**
     * @param line           - Line read in from file
     * @param preferenceName - Name of the Preference.
     * @return - Preference Value
     */
    private static String stripLine(String line, String preferenceName) {
        return line.substring(preferenceName.length()).strip();
    }

    /**
     *
     */
    public static void printOutPrefs() {
        log.info(String.format("Username is %s ", Preferences.getUserName()));
        log.info(String.format("Password is %s  ", Preferences.getPassword()));
        log.info(String.format("minMSRP is %s  ", Preferences.getMinMSRP()));
        log.info(String.format("maxBid is  %s  ", Preferences.getMaxBid()));
        log.info(String.format("Zip Code is  %s ", Preferences.getBidZipCode()));
        log.info(String.format("Zip Code Distance is  %s  ", Preferences.getBidZipDistance()));
        log.info(String.format("Item Status is  %s  ", Preferences.getItemStatus()));
        log.info(String.format("Exclude is  %s  ", Preferences.getExcludedWords()));
        log.info(String.format("AutoBid is %s  ", Preferences.isAutoBid()));
    }

    /**
     * Verifies the Preferences are correct. Examples of incorrect Preferences would
     * be minMSRP > MaxBid, no username or password.
     *
     * @return - True iff the preferences are valid
     */
    public static boolean verifyPreferences() {
        boolean validPrefs = true;

        // Check that all prefs were entered
        validPrefs &= Preferences.getUserName() != null;
        validPrefs &= Preferences.getPassword() != null;
        validPrefs &= !Double.isNaN(Preferences.getMinMSRP());
        validPrefs &= !Double.isNaN(Preferences.getMaxBid());

        // MaxBid > minMsrp does not make any sense!
        validPrefs &= Preferences.getMinMSRP() > Preferences.getMaxBid();

        return validPrefs;
    }


}
