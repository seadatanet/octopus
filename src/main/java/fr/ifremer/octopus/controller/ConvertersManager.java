package fr.ifremer.octopus.controller;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.medatlas.exceptions.MedatlasWriterException;
import fr.ifremer.medatlas.input.MedatlasInputFileManager;
import fr.ifremer.mgd.MGD77Manager;
import fr.ifremer.mgd.MGD77V81Manager;
import fr.ifremer.mgd.MGD77V98Manager;
import fr.ifremer.mgd.MGDException;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.octopus.utils.SDNVocabs;
import fr.ifremer.seadatanet.cfpoint.exceptions.CFPointException;
import fr.ifremer.seadatanet.cfpoint.input.CFReader;
import fr.ifremer.seadatanet.odv.input.OdvReader;
import fr.ifremer.seadatanet.odv.output.OdvException;
import fr.ifremer.sismer_tools.coupling.CouplingRecord;
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
			String edmo;
			switch (inputFormat) {
			case MEDATLAS_NON_SDN:
				edmo = PreferencesManager.getInstance().getEdmoCode();
				if (edmo==null || edmo.isEmpty()){
					throw new OctopusException("you must set EDMO code in settings panel"); //TODO
				}
				conv = new MedatlasInputFileManager(inputFile.getAbsolutePath(), SDNVocabs.getInstance().getCf(), Integer.valueOf(edmo));
				break;
			case MEDATLAS_SDN:
				conv = new MedatlasInputFileManager(inputFile.getAbsolutePath(), SDNVocabs.getInstance().getCf());
				break;
			case ODV_SDN:
				conv = new OdvReader(inputFile.getAbsolutePath(), SDNVocabs.getInstance().getCf());
				break;
			case CFPOINT:
				conv = new CFReader(inputFile.getAbsolutePath());
				break;
			case MGD_81:
				edmo = PreferencesManager.getInstance().getEdmoCode();
				if (edmo==null|| edmo.isEmpty()){
					throw new OctopusException("you must set EDMO code in settings panel"); //TODO
				}
				conv= new MGD77V81Manager(inputFile.getAbsolutePath(), Integer.valueOf(edmo));
				break;
			case MGD_98:
				edmo = PreferencesManager.getInstance().getEdmoCode();
				if (edmo==null){
					throw new OctopusException("you must set EDMO code in settings panel"); //TODO
				}
				conv = new MGD77V98Manager(inputFile.getAbsolutePath(), Integer.valueOf(edmo));
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
		case MGD_81:
		case MGD_98:
			LOGGER.warn("MGD files does not contains local CDI Ids");
			return null;
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
		case MGD_81:
		case MGD_98:
			LOGGER.warn("MGD files does not contains local CDI Ids");
		default:
			LOGGER.error("undefined input format"); // TODO
			return false;
		}
	}

	public List<CouplingRecord>  print(List<String> cdiList, String outputFileAbsolutePath, Format outputFormat, String outputLocalCdiId) throws MedatlasWriterException, OdvException, CFPointException, OctopusException, MGDException {
		
		if (inputFormat== Format.MGD_81 || inputFormat== Format.MGD_98){
			if (outputLocalCdiId == null){
				throw new OctopusException("output local CDI Id must be defined for MGD files");// TODO
			}
		}
			
			
		String titleComplement="";
		if (outputFormat==Format.CFPOINT){
			titleComplement = TITLE_COMPLEMENT;
		}
		switch (inputFormat) {
		case MEDATLAS_SDN:
		case MEDATLAS_NON_SDN:
			return ((MedatlasInputFileManager)conv).print(cdiList, outputFileAbsolutePath, outputFormat, titleComplement,  unitsTranslationFileName);
		case ODV_SDN:
			return ((OdvReader)conv).print(cdiList, outputFileAbsolutePath, outputFormat, titleComplement, unitsTranslationFileName);
		case CFPOINT:
			return ((CFReader)conv).print(cdiList, outputFileAbsolutePath, titleComplement);
		case MGD_81:
		case MGD_98:
			return ((MGD77Manager)conv).print( outputFileAbsolutePath, outputLocalCdiId);
		default:
			throw new OctopusException("undefined input format");// TODO
		}
	}
	public void close()  {
		try {
			switch (inputFormat) {
			case MEDATLAS_SDN:
			case MEDATLAS_NON_SDN:
				((MedatlasInputFileManager)conv).close();
				break;
			case ODV_SDN:
				((OdvReader)conv).close();
				break;
			case CFPOINT:
				((CFReader)conv).close();
				break;
			case MGD_81:
			case MGD_98:
				((MGD77Manager)conv).close();
				break;
			default:
				LOGGER.error("undefined input format");// TODO
			}
		} catch (Exception e) {
			LOGGER.error("error while closing input file ");// TODO
		}

	}

}
