package fr.ifremer.octopus.controller.checker;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.medatlas.input.MedatlasInputFileManager;
import fr.ifremer.sismer_tools.seadatanet.Format;


public class MedatlasFormatChecker extends FormatChecker {
	private static final Logger LOGGER = LogManager.getLogger(MedatlasFormatChecker.class);

	@Override
	public Format check(File f) throws Exception {
		try {
			// use strict check
			MedatlasInputFileManager mgr = new MedatlasInputFileManager(f.getAbsolutePath(), true);
			if (mgr.isSDNMedatlas())
			{
				return Format.MEDATLAS_SDN;
			}else{
				return Format.MEDATLAS_NON_SDN;
			}
		
		} catch (Exception e1) {
			LOGGER.error(e1);
			throw e1 ;
		}
		
	}

}
