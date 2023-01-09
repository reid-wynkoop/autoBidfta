package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author speed
 *
 */
public class AutoBidder {
	
	private static final Logger Log = LoggerFactory.getLogger(AutoBidder.class);

	public static void main(String[] args) {

		// Read in the prefs.txt and initialize the Preferences
		Preferences.init();

		if (!Preferences.verifyPreferences()) {
			Log.error("There were errors with the given preferences. Cannot continue with AutoBidder");
			System.exit(0);
		}
		
		BidftaWebDriver driver = new BidftaWebDriver();
		driver.drive();
		
		

	}

}
