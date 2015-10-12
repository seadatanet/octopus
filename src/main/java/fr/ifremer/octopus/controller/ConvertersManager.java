package fr.ifremer.octopus.controller;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.medatlas.exceptions.MedatlasWriterException;
import fr.ifremer.medatlas.input.MedatlasInputFileManager;
import fr.ifremer.octopus.utils.SDNVocabs;
import fr.ifremer.seadatanet.cfpoint.exceptions.CFPointException;
import fr.ifremer.seadatanet.cfpoint.input.CFReader;
import fr.ifremer.seadatanet.odv.input.OdvReader;
import fr.ifremer.seadatanet.odv.output.OdvException;
import fr.ifremer.sismer_tools.seadatanet.Format;

public class ConvertersManager {
	private static final Logger LOGGER = LogManager.getLogger(ConvertersManager.class);
	private static final String TITLE_COMPLEMENT = " from Octopus";// FIXME version
	private static final String unitsTranslationFileName = "octopusUnitsTranslation.xml";
	private Object conv;
	private Format inputFormat;

	public ConvertersManager(File inputFile, Format inputFormat) throws OctopusException  {
		this.inputFormat = inputFormat;

		try{
			switch (inputFormat) {
			case MEDATLAS_SDN:
				conv = new MedatlasInputFileManager(inputFile.getAbsolutePath(), SDNVocabs.getInstance().getCf());
				break;
			case ODV_SDN:
				conv = new OdvReader(inputFile.getAbsolutePath(), SDNVocabs.getInstance().getCf());
				break;
			case CFPOINT:
				conv = new CFReader(inputFile.getAbsolutePath());
				break;
			default:
				break;
			}
		}catch(Exception e){
			LOGGER.error(e.getMessage());
			throw new OctopusException("error while initializing input file reader"); // TODO
		}

	}


	public List<String> getInputFileCdiIdList() {
		switch (inputFormat) {
		case MEDATLAS_SDN:
			return ((MedatlasInputFileManager)conv).getInputFileCdiIdList();
		case ODV_SDN:
			return ((OdvReader)conv).getInputFileCdiIdList();
		case CFPOINT:
			return ((CFReader)conv).getInputFileCdiIdList();
		default:
			return null;
		}
	}


	public boolean containsCdi(String cdi) {
		switch (inputFormat) {
		case MEDATLAS_SDN:
			return ((MedatlasInputFileManager)conv).containsCdi(cdi);
		case ODV_SDN:
			return ((OdvReader)conv).containsCdi(cdi);
		case CFPOINT:
			return ((CFReader)conv).containsCdi(cdi);
		default:
			LOGGER.error("undefined input format"); // TODO
			return false;
		}
	}


	public void print(List<String> cdiList, String outputFileAbsolutePath, Format outputFormat) throws MedatlasWriterException, OdvException, CFPointException {
		String titleComplement="";
		if (outputFormat==Format.CFPOINT){
			titleComplement = TITLE_COMPLEMENT;
		}
		switch (inputFormat) {
		case MEDATLAS_SDN:
			((MedatlasInputFileManager)conv).print(cdiList, outputFileAbsolutePath, outputFormat, titleComplement,  unitsTranslationFileName);
			break;
		case ODV_SDN:
			((OdvReader)conv).print(cdiList, outputFileAbsolutePath, outputFormat, titleComplement, unitsTranslationFileName);
			break;
		case CFPOINT:
			((CFReader)conv).print(cdiList, outputFileAbsolutePath, titleComplement);
			break;
		default:
			LOGGER.error("undefined input format"); // TODO
		}

	}
	public void close()  {
		try {
			switch (inputFormat) {
			case MEDATLAS_SDN:
				((MedatlasInputFileManager)conv).close();
				break;
			case ODV_SDN:
				((OdvReader)conv).close();
				break;
			case CFPOINT:
				((CFReader)conv).close();
				break;
			default:
				LOGGER.error("undefined input format");// TODO
			}
		} catch (Exception e) {
			LOGGER.error("error while closing input file ");// TODO
		}

	}

}
