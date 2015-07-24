package fr.ifremer.octopus.io.driver.impl;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.model.Format;
import fr.ifremer.seadatanet.cfpoint.util.ParameterNames;

public class CFPointDriverImpl extends Driver {
	private static final Logger LOGGER = LogManager.getLogger(CFPointDriverImpl.class);

	
	@Override
	public boolean canOpen(String file) throws IOException {
		boolean canOpen = false;
		try {
			NetcdfFile inputFile = NetcdfFile.open(file);
			Variable varCDI = inputFile.findVariable(ParameterNames.SDN_LOCAL_CDI_ID);
			if (varCDI==null){
				LOGGER.info("file is netCDF but is not SDN CFPoint format: missing LOCAL_CDI_ID variable");
			}else{
				format = Format.CFPOINT;
				canOpen = true;
			}
			inputFile.close();
		} catch (IOException e) {
			LOGGER.debug(e.getMessage());
		}
		return canOpen;		
	}


}
