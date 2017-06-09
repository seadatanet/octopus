package fr.ifremer.octopus.io.driver.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.sismer_tools.seadatanet.Format;

public class OdvSDNDriverImpl extends Driver {
	private static final Logger LOGGER = LogManager.getLogger(OdvSDNDriverImpl.class);
	public static final String UTF8_BOM = "\uFEFF";

	@Override
	public boolean canOpen(String file)  {
		boolean canOpen = false;
		BufferedReader reader  = null;
		try{
			reader = new BufferedReader(new FileReader(file)) ;
			String firstLine = reader.readLine();
			// TODO BGT specs: define more criteria here?
			// Actual criteria: if first character is "//", the file is a odv file
			
			if (firstLine.startsWith(UTF8_BOM)){
				firstLine = firstLine.substring(1, firstLine.length());
			}
			if( firstLine.trim().startsWith("//")){
				format = Format.ODV_SDN;
				canOpen= true;
			}else{
				canOpen= false;
			}

		}catch(Exception e){
			LOGGER.error(e.getMessage());
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				LOGGER.debug(e.getMessage());
			}
		}
		return canOpen;
	}


}
