package fr.ifremer.octopus.controller.checker;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.seadatanet.cfpoint.input.CFReader;
import fr.ifremer.sismer_tools.seadatanet.Format;

public class CFPointFormatChecker extends FormatChecker {
	private static final Logger LOGGER = LogManager.getLogger(CFPointFormatChecker.class);

	@Override
	public Format check(File f) throws Exception {
		
		try {
			CFReader reader = new CFReader(f.getAbsolutePath());
			if (reader.getValidator().hasErrors()){
				throw new OctopusException("File " + f.getName() + " has some errors");
			}else{
				return Format.CFPOINT;
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw e;
		}

	}

}
