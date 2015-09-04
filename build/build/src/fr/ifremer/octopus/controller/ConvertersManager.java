package fr.ifremer.octopus.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.medatlas.Medatlas2CFPointConverter;
import fr.ifremer.medatlas.exceptions.ConverterException;
import fr.ifremer.medatlas.exceptions.MedatlasReaderException;
import fr.ifremer.octopus.model.Format;
import fr.ifremer.octopus.utils.SDNVocabs;
import fr.ifremer.seadatanet.odvsdn2cfpoint.OdvSDN2CFPointConverter;

public class ConvertersManager {
	private static final Logger LOGGER = LogManager.getLogger(ConvertersManager.class);
	private static final String CONV_SUBTITLE = " from Octopus";
	private static final String unitsTranslationFileName = "unitsTranslation.xml";
	private Object conv;
	private Format format;

	public ConvertersManager(Format format) throws ConverterException, VocabularyException, fr.ifremer.seadatanet.odvsdn2cfpoint.exceptions.ConverterException {

		this.format = format;
		switch (format) {
		case MEDATLAS_SDN:
			createMed2CfConv();
			break;
		case ODV_SDN:
			createOdv2CfConv();
			break;

		default:
			break;
		}

	}

	private void createOdv2CfConv() throws fr.ifremer.seadatanet.odvsdn2cfpoint.exceptions.ConverterException, VocabularyException {
		conv = new OdvSDN2CFPointConverter(CONV_SUBTITLE, 
				SDNVocabs.getInstance().getCf(),
				unitsTranslationFileName);

	}

	private void createMed2CfConv() throws ConverterException, VocabularyException {
		conv = new Medatlas2CFPointConverter(CONV_SUBTITLE, 
				SDNVocabs.getInstance().getCf(),
				unitsTranslationFileName);
		

	}

	public String processFile(String in, String outputPath,
			boolean mono) throws OctopusException   {
		
		String resOutput="" ;
		switch (format) {
		case MEDATLAS_SDN:
			try {
				fr.ifremer.medatlas.Result res = ((Medatlas2CFPointConverter)conv).processFile(in, outputPath, mono, true);
				resOutput = res.getOutputPath();
			} catch (MedatlasReaderException | ConverterException e) {
				throw new OctopusException(e.getMessage());
			}
			break;
		case ODV_SDN:
			fr.ifremer.seadatanet.odvsdn2cfpoint.Result res = ((OdvSDN2CFPointConverter)conv).processFile(in, outputPath, mono, true);
			resOutput = res.getOutputPath();
			break;

		default:
			break;
		}
		return  resOutput;
		

	}

}
