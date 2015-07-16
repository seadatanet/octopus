package fr.ifremer.octopus.io.dataManager;

import java.io.File;

/**
 * 
 * @author jmens
 *
 * @param <T>
 *
 */
public abstract class AbstractDataFileManager<T> {

	/**
	 * Current file
	 */
	protected File file;
	
	/**
	 * 
	 * @param path
	 * @param dataset
	 */
	public AbstractDataFileManager(String path) {
		this.file = new File(path);
	}
	
}
