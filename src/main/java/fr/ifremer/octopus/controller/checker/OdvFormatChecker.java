package fr.ifremer.octopus.controller.checker;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.utils.SDNVocabs;
import fr.ifremer.seadatanet.odv.input.OdvReader;
import fr.ifremer.sismer_tools.seadatanet.Format;

public class OdvFormatChecker extends FormatChecker {
	private static final Logger LOGGER = LogManager.getLogger(OdvFormatChecker.class);

	@Override
	public Format check(File f) throws Exception {
		
		try {
			OdvReader mgr = new OdvReader(f.getAbsolutePath(), SDNVocabs.getInstance().getCf(), SDNVocabs.getInstance().getCSRListManager());
			return Format.ODV_SDN;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}

	}

}
