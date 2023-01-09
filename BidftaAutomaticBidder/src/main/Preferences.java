package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores local preferences like username/password, bidding location, max bid,
 * etc.
 * 
 * @author speed
 *
 */
public class Preferences {

	private Preferences() {
		throw new IllegalStateException("Preference class");
	}

	private static String userName;
	private static String password;
	private static double maxBid;
	private static double minMSRP;
	private static List<String> bidLocations;
	private static List<String> itemStatus;
	private static List<String> excludedWords;
	private static boolean autoBid;
	private static final Logger Log = LoggerFactory.getLogger(Preferences.class);

	// Reads in the prefs.txt file and initialized the preferences
	public static void init() {

		File f = new File("prefs.txt");
		if (f.exists()) {
			Log.info("FILE DOES EXIST");

			try (BufferedReader reader = new BufferedReader(new FileReader(f));) {
				String line;
				while ((line = reader.readLine()) != null) {
					Log.info(line);
					// Lines starting with '#' are comments.
					if (!line.strip().isEmpty() && !line.startsWith("#")) {
						Preferences.setPreference(line);
					}
				}
				Preferences.printOutPrefs();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			Log.error("FILE DOES NOT EXIST");
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
				Double percent = Double.parseDouble(line.substring(0, line.length() - 1));
				// i.e. if minMsrp is set
				if (!Double.isNaN(Preferences.minMSRP)) {
					Preferences.maxBid = Preferences.minMSRP * (percent / 100);
				}
				else {
					Log.error("MinMSRP was null when assigning MaxBid. Move MinMSRP before MaxBid if prefs.txt");
				}
			}

		}
		else if (line.startsWith(Constants.BID_LOCATIONS)) {
			line = stripLine(line, Constants.BID_LOCATIONS);
			Preferences.bidLocations = Arrays.asList(line.split(","));
		}
		else if (line.startsWith(Constants.ITEM_STATUS)) {
			line = stripLine(line, Constants.ITEM_STATUS);
			Preferences.itemStatus = Arrays.asList(line.split(","));

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

	/**
	 * 
	 * @param line
	 * @param preferenceValue
	 * @return
	 */
	private static String stripLine(String line, String preferenceValue) {
		return line.substring(preferenceValue.length()).strip();
	}

	/**
	 * 
	 */
	public static void printOutPrefs() {
		Log.info(String.format("Username is \" %s \" ", Preferences.getUserName()));
		Log.info(String.format("Password is \" %s \" ", Preferences.getPassword()));
		Log.info(String.format("minMSRP is \" %s \" ", Preferences.getMinMSRP()));
		Log.info(String.format("maxBid is \" %s \" ", Preferences.getMaxBid()));
		Log.info(String.format("Locations is \" %s \" ", Preferences.getBidLocations()));
		Log.info(String.format("Item Status is \" %s \" ", Preferences.getItemStatus()));
		Log.info(String.format("Exclude is \" %s \" ", Preferences.getExcludedWords()));
		Log.info(String.format("AutoBid is \" %s \" ", Preferences.isAutoBid()));
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

	/**
	 * @return the userName
	 */
	public static String getUserName() {
		return Preferences.userName;
	}

	/**
	 * @return the password
	 */
	public static String getPassword() {
		return Preferences.password;
	}

	/**
	 * @return the maxBid
	 */
	public static double getMaxBid() {
		return Preferences.maxBid;
	}

	/**
	 * @return the minMSRP
	 */
	public static double getMinMSRP() {
		return Preferences.minMSRP;
	}

	/**
	 * @return the bidLocations
	 */
	public static List<String> getBidLocations() {
		return Preferences.bidLocations;
	}

	/**
	 * @return the LOG
	 */
	public static Logger getLOG() {
		return Preferences.Log;
	}

	/**
	 * @return the itemStatus
	 */
	public static List<String> getItemStatus() {
		return Preferences.itemStatus;
	}

	/**
	 * @return the excludedWords
	 */
	public static List<String> getExcludedWords() {
		return Preferences.excludedWords;
	}

	/**
	 * @return the autoBid
	 */
	public static boolean isAutoBid() {
		return Preferences.autoBid;
	}

}
