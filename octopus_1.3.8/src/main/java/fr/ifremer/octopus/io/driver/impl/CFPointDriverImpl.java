package fr.ifremer.octopus.io.driver.impl;

import java.io.IOException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;
import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.seadatanet.cfpoint.util.ParameterNames;
import fr.ifremer.sismer_tools.seadatanet.Format;

public class CFPointDriverImpl extends Driver {
	private static final Logger LOGGER = LogManager.getLogger(CFPointDriverImpl.class);

	
	@Override
	public boolean canOpen(String file) throws IOException {
		ResourceBundle messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());
		boolean canOpen = false;
		try {
			NetcdfFile inputFile = NetcdfFile.open(file);
			Variable varCDI = inputFile.findVariable(ParameterNames.SDN_LOCAL_CDI_ID);
			if (varCDI==null){
				LOGGER.debug(messages.getObject("driver.netcdfIsNotSDN"));
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
