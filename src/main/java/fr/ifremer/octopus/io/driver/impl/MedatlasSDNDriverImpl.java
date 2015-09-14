package fr.ifremer.octopus.io.driver.impl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.medatlas.utils.MedatlasKeywords;
import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.model.Format;

public class MedatlasSDNDriverImpl extends Driver {
	private static final Logger LOGGER = LogManager.getLogger(MedatlasSDNDriverImpl.class);

	@Override
	public boolean canOpen(String file)  {
		boolean canOpen = false;
		BufferedReader reader = null;
		String currentLine;
		try {
			reader = new BufferedReader(new FileReader(file));
			String firstLine = reader.readLine();
			// TODO BGT specs: define more criteria here?
			// Actual criteria: if first character is "*", the file is a medatlas file
			if( firstLine.trim().startsWith("*")){
				format = Format.MEDATLAS_NON_SDN;
				canOpen=true;
				
				while((currentLine = reader.readLine()) != null) {
					if(currentLine.contains(MedatlasKeywords.SDN_PARAMETER_MAPPING_KEYWORD)) { 
						format = Format.MEDATLAS_SDN;
						canOpen=true;
					}
				}
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.debug(e.getMessage());
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
