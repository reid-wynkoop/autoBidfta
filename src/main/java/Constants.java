/**
 * @author speed
 */
public class Constants {

    private Constants() {
        throw new IllegalStateException("Constant class");
    }

    public static final String BIDFTA_URL = "https://www.bidfta.com";

    public static final String BIDFTA_DASHBOARD_URL = "https://www.bidfta.com/account/dashboard";

    public static final String USERNAME = "Username:";
    public static final String PASSWORD = "Password:";
    public static final String MIN_MSRP = "minMSRP:";
    public static final String MAX_BID = "maxBID:";
    public static final String ITEM_STATUS = "itemStatus:";
    public static final String EXCLUDE = "exclude:";
    public static final String AUTO_BID = "autoBid:";

    public static final String BID_ZIP_DISTANCE = "bidZipDistance:";

    public static final String BID_ZIP_CODE = "bidZipCode:";

    // Xpaths
    public static final String AUCTION_BID_XPATH = "/html/body/div[1]/div/div[4]/main/div/div[1]/aside/div/div/div[2]/a";

}
