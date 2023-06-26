import lombok.extern.java.Log;

/**
 * @author speed
 *
 */
@Log
public class AutoBidder {
	

	public static void main(String[] args) {

		// Read in the prefs.txt and initialize the Preferences
		Preferences.init();

		if (!Preferences.verifyPreferences()) {
			log.severe("There were errors with the given preferences. Cannot continue with AutoBidder");
			System.exit(0);
		}
		
		BidftaWebDriver driver = new BidftaWebDriver();
		driver.drive();
		
		

	}

}
