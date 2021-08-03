package fr.ifremer.octopus.io.driver.impl;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.seadatanet.cfpoint.exceptions.CFPointException;
import fr.ifremer.seadatanet.cfpoint.input.CFModel.NETCDF_FORMAT;
import fr.ifremer.seadatanet.cfpoint.input.CFReader;
import fr.ifremer.sismer_tools.seadatanet.Format;
import ucar.nc2.NetcdfFile;

public class CFPointDriverImpl extends Driver {
	private static final Logger LOGGER = LogManager.getLogger(CFPointDriverImpl.class);
	
	NETCDF_FORMAT netCdfFormat;

	@Override
	public boolean canOpen(String file) throws IOException {
		boolean canOpen = false;
		try {
			NetcdfFile inputFile = NetcdfFile.open(file);

			format = Format.CFPOINT;
			canOpen = true;

			CFReader reader = new CFReader(file, false);
			
			netCdfFormat = reader.detectInputFormat(true);
			if (netCdfFormat == NETCDF_FORMAT.HFRadar) {
				format = Format.CFPOINT_HFRADAR;
			}
			else if (netCdfFormat == NETCDF_FORMAT.EGOGlider) {
				format = Format.CFPOINT_EGOGLIDER;
			}
			
			inputFile.close();
		} catch (IOException e) {
			LOGGER.debug(e.getMessage());
		} catch (CFPointException e) {
			LOGGER.debug(e.getMessage());
		}
		return canOpen;
	}
	
	public NETCDF_FORMAT getNetCdfFormat() {
		return netCdfFormat;
	}
}
