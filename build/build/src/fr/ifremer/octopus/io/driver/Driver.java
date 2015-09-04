package fr.ifremer.octopus.io.driver;

import java.io.IOException;

import fr.ifremer.octopus.model.Format;


/**
 * 
 * @author Altran
 *
 */
public abstract class Driver {

	protected  Format format = null;
	/**
	 * 
	 * @param file
	 * @return true if the file can be opened, false otherwise
	 * @throws IOException if file can not be open with this driver
	 */
	
	public abstract boolean canOpen(String file) throws IOException;

	public Format getFormat() {
		return format;
	}

}
