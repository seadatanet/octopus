package fr.ifremer.octopus.io.driver.impl;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.sismer_tools.seadatanet.Format;
import ucar.nc2.NetcdfFile;

public class CFPointDriverImpl extends Driver {
	private static final Logger LOGGER = LogManager.getLogger(CFPointDriverImpl.class);

	@Override
	public boolean canOpen(String file) throws IOException {
		boolean canOpen = false;
		try {
			NetcdfFile inputFile = NetcdfFile.open(file);

			format = Format.CFPOINT;
			canOpen = true;

			inputFile.close();
		} catch (IOException e) {
			LOGGER.debug(e.getMessage());
		}
		return canOpen;
	}
}
