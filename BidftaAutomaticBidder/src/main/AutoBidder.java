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
		// TODO Auto-generated method stub
		// WebDriver driver = new FirefoxDriver();
		// driver.get(Constants.BIDFTA_URL);
//		try {
//			d = Desktop.getDesktop();
//			d.browse(java.net.URI.create(Constants.BIDFTA_URL));
//
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		try {
//			driver.wait(1000 * 5);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		// driver.quit();

	}

}
