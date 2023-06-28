/**
 * Utility Class
 */
public final class Utils {

    public static String createFilteredAuctionUrl(String href) {
        return href + Preferences.getItemStatusUrlFilter();
    }

}
