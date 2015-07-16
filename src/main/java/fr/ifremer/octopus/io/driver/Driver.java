package fr.ifremer.octopus.io.driver;

import java.io.IOException;

import fr.ifremer.octopus.model.Format;


/**
 * 
 * @author Altran
 *
 */
public interface Driver {

	/**
	 * 
	 * @param file
	 * @return true if the file can be opened, false otherwise
	 * @throws IOException 
	 */
	
	boolean canOpen(String file) throws IOException;

	Format getFormat();

}
