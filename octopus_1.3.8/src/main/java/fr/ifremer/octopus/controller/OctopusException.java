package fr.ifremer.octopus.controller;

public class OctopusException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OctopusException() {
	}

	public OctopusException(String message) {
		super(message);
	}

	public OctopusException(Throwable cause) {
		super(cause);
	}

	public OctopusException(String message, Throwable cause) {
		super(message, cause);
	}

	public OctopusException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
