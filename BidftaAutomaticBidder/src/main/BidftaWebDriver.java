package main;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author speed
 *
 */
public class BidftaWebDriver {

	private WebDriver driver;
	private static final Logger Log = LoggerFactory.getLogger(BidftaWebDriver.class);

	/**
	 * New Bidfta Driver
	 */
	public BidftaWebDriver() {
		// Could add check for prefered browser?
		FirefoxOptions options = new FirefoxOptions();
		options.addArguments("-private");
		this.driver = new FirefoxDriver(options);

	}

	public void drive() {
		driver.get(Constants.BIDFTA_URL);
		// Login with creds provided in prefs.txt
		boolean loggedIn = login();

		if (!loggedIn) {
			// error message
			System.exit(0);
		}

	}

	/**
	 * Attempts to log into BidFta.com with given credentials
	 * 
	 * @return true if successfully logged in, false otherwise
	 */
	private boolean login() {
		// click on the Login button
		boolean loggedIn = false;
		int loginAttempts = 0;
		waitBid(2);
		WebElement element = driver.findElement(By.id("loginDisplay"));
		if (element == null) {
			Log.warn("Did not find button");
			return loggedIn;
		}
		element.click();
		waitBid(2);
		while (loginAttempts < 4) {
			element = driver.findElement(By.id("username"));
			Log.info(Preferences.getUserName());
			element.sendKeys(Preferences.getUserName());
			waitBid(2);
			element = driver.findElement(By.id("password"));
			Log.info(Preferences.getPassword());
			element.sendKeys(Preferences.getPassword());
			waitBid(3);
			element.sendKeys(Keys.ENTER);
			Log.info(element.getTagName());
			element.click();
			waitBid(3);
			if (Constants.BIDFTA_URL.equals(this.driver.getCurrentUrl())) {
				loggedIn = true;
				break;
			}

			loginAttempts++;
		}

		return loggedIn;

	}

	/**
	 * 
	 * @param timeoutMillis
	 */
	private void waitBid(double seconds) {
		long timeMilis = (long) (seconds * 1000);

		synchronized (this.driver) {
			try {
				driver.wait(timeMilis);
			}
			catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				e.printStackTrace();
			}
		}
	}
}
