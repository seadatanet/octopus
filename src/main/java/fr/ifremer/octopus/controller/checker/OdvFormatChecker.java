package fr.ifremer.octopus.controller.checker;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.utils.SDNVocabs;
import fr.ifremer.seadatanet.odvsdn2cfpoint.odv.OdvReader;

public class OdvFormatChecker extends FormatChecker {
	private static final Logger LOGGER = LogManager.getLogger(OdvFormatChecker.class);

	@Override
	public void check(File f) throws Exception {
		
		try {
			OdvReader mgr = new OdvReader(f.getAbsolutePath(), SDNVocabs.getInstance().getCf());
			mgr.parseMetadata();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}

	}

}
