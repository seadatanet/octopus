package fr.ifremer.octopus.utils;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Utils class for network checks and operations.
 */
public final class NetworkUtils {

	/**
	 * Simple internet connection check, by opening a TCP socket to a well-known host.
	 * Using a plain socket on port 80 avoids SSL certificate validation issues
	 * that can occur when the JRE truststore does not contain the remote server's CA.
	 *
	 * @return true if the connection could be made.
	 */
	public static boolean isInternetUp() {
		try (Socket socket = new Socket()) {
			socket.connect(new InetSocketAddress("www.google.com", 80), 3000);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
