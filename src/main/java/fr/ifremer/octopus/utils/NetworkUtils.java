package fr.ifremer.octopus.utils;

import java.net.URL;
import java.net.URLConnection;

/**
 * Utils class for network checks and operations.
 */
public final class NetworkUtils {

	/**
	 * Simple internet connection check, by connecting to Google.
	 * 
	 * @return true if the connection could be made.
	 */
	public static boolean isInternetUp() {
		try {
			URL url = new URL("https://www.google.com/");
			URLConnection connection = url.openConnection();
			connection.connect();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
