package fr.ifremer.octopus.io.driver.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
		BufferedReader buffer  = null;
		try{
			buffer = new BufferedReader(new FileReader(file)) ;
			canOpen=checkFirstLineOk(buffer);

		}catch(Exception e){
			LOGGER.error(e.getMessage());
		}finally{
			try {
				buffer.close();
			} catch (IOException e) {
				LOGGER.debug(e.getMessage());
			}
		}

		if (!canOpen){

			FileInputStream fis = null;
			try{
				fis = new FileInputStream(file);
				buffer = new BufferedReader(new InputStreamReader(fis, "UTF8"));
				canOpen=checkFirstLineOk(buffer);
			}catch(Exception e){
				LOGGER.error(e.getMessage());
			}finally{
				try{
					fis.close();
					buffer.close();
				} catch (IOException e) {
					LOGGER.debug(e.getMessage());
				}
			}
		}
		return canOpen;
	}
private boolean checkFirstLineOk (BufferedReader buffer){
	try{
		String firstLine = buffer.readLine();
		
		if (firstLine.startsWith(UTF8_BOM)){
			firstLine = firstLine.substring(1, firstLine.length());
		}
		if( firstLine.trim().startsWith("//")){
			format = Format.ODV_SDN;
			return  true;
		}else{
			return false;
		}
	}catch(Exception e){
		LOGGER.error(e.getMessage());
	}
	return false;
}



}
