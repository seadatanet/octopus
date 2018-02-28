package fr.ifremer.octopus.controller;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

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
import fr.ifremer.seadatanet.odvsdn2cfpoint.exceptions.ConverterException;
import fr.ifremer.sismer_tools.coupling.CouplingRecord;
import fr.ifremer.sismer_tools.seadatanet.Format;

public class ConvertersManager {
	private static final Logger LOGGER = LogManager.getLogger(ConvertersManager.class);
	private static final ResourceBundle  aboutBundle = ResourceBundle.getBundle("bundles/about", PreferencesManager.getInstance().getLocale());

	private static final String originatorSoftwareName ="Octopus";
	private static final String originatorSoftwareVersion =aboutBundle.getString("about.version");
	
	private static final String unitsTranslationFileName = "octopusUnitsTranslation.xml";
	private Object conv;
	private Format inputFormat;
	protected  ResourceBundle messages;

	public ConvertersManager(File inputFile, Format inputFormat) throws OctopusException  {
		this.inputFormat = inputFormat;
		messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());

		try{
			String edmo;
			switch (inputFormat) {
			case MEDATLAS_NON_SDN:
				edmo = PreferencesManager.getInstance().getEdmoCode();
				if (edmo==null || edmo.isEmpty()){
					throw new OctopusException(messages.getString("converter.setEdmoInSettings")); 
				}
				conv = new MedatlasInputFileManager(inputFile.getAbsolutePath(), SDNVocabs.getInstance().getCf(), SDNVocabs.getInstance().getCSRListManager(), Integer.valueOf(edmo));
				break;
			case MEDATLAS_SDN:
				conv = new MedatlasInputFileManager(inputFile.getAbsolutePath(), SDNVocabs.getInstance().getCf(), SDNVocabs.getInstance().getCSRListManager());
				break;
			case ODV_SDN:
				conv = new OdvReader(inputFile.getAbsolutePath(), SDNVocabs.getInstance().getCf(), SDNVocabs.getInstance().getCSRListManager());
				break;
			case CFPOINT:
				conv = new CFReader(inputFile.getAbsolutePath(), SDNVocabs.getInstance().getCf(), SDNVocabs.getInstance().getCSRListManager());
				break;
			case MGD_81:
				edmo = PreferencesManager.getInstance().getEdmoCode();
				if (edmo==null|| edmo.isEmpty()){
					throw new OctopusException(messages.getString("converter.setEdmoInSettings"));
				}
				conv= new MGD77V81Manager(inputFile.getAbsolutePath(), Integer.valueOf(edmo));
				break;
			case MGD_98:
				edmo = PreferencesManager.getInstance().getEdmoCode();
				if (edmo==null){
					throw new OctopusException(messages.getString("converter.setEdmoInSettings")); 
				}
				conv = new MGD77V98Manager(inputFile.getAbsolutePath(), Integer.valueOf(edmo));
				break;
			default:
				break;
			}
		}catch(Exception e){
			LOGGER.error(e.getMessage());
			throw new OctopusException(messages.getString("converter.errorInitInputReader")); 
		}

	}


	public List<String> getInputFileCdiIdList() {
		switch (inputFormat) {
		case MEDATLAS_SDN:
		case MEDATLAS_NON_SDN:
			return ((MedatlasInputFileManager)conv).getInputFileCdiIdList();
		case ODV_SDN:
			return ((OdvReader)conv).getInputFileCdiIdList();
		case CFPOINT:
			return ((CFReader)conv).getInputFileCdiIdList();
		case MGD_81:
		case MGD_98:
			LOGGER.warn(messages.getString("converter.mgdNotContainsCDI"));
			return null;
		default:
			return null;
		}
	}


	public boolean containsCdi(String cdi) {
		switch (inputFormat) {
		case MEDATLAS_SDN:
		case MEDATLAS_NON_SDN:
			return ((MedatlasInputFileManager)conv).containsCdi(cdi);
		case ODV_SDN:
			return ((OdvReader)conv).containsCdi(cdi);
		case CFPOINT:
			return ((CFReader)conv).containsCdi(cdi);
		case MGD_81:
		case MGD_98:
			LOGGER.warn(messages.getString("converter.mgdNotContainsCDI"));
		default:
			LOGGER.error(messages.getString("abstractcontroller.unrecognizedInputFormat")); 
			return false;
		}
	}

	public List<CouplingRecord>  print(List<String> cdiList, String outputFileAbsolutePath, Format outputFormat, String outputLocalCdiId) throws MedatlasWriterException, OdvException, CFPointException, OctopusException, MGDException, ConverterException {

		if (inputFormat== Format.MGD_81 || inputFormat== Format.MGD_98){
			if (outputLocalCdiId == null){
				throw new OctopusException(messages.getString("converter.outputCDIMustBeDefinedForMGD"));
			}
		}


		//String titleComplement="";
//		if (outputFormat==Format.CFPOINT){
//			titleComplement = TITLE_COMPLEMENT;
//		}
		switch (inputFormat) {
		case MEDATLAS_SDN:
		case MEDATLAS_NON_SDN:
			return ((MedatlasInputFileManager)conv).print(cdiList, outputFileAbsolutePath,  originatorSoftwareName, originatorSoftwareVersion,  unitsTranslationFileName, outputFormat);
		case ODV_SDN:
			return ((OdvReader)conv).print(cdiList, outputFileAbsolutePath, originatorSoftwareName, originatorSoftwareVersion, unitsTranslationFileName, outputFormat);
		case CFPOINT:
			return ((CFReader)conv).print(cdiList, outputFileAbsolutePath, originatorSoftwareName, originatorSoftwareVersion ,unitsTranslationFileName, outputFormat);
		case MGD_81:
		case MGD_98:
			return ((MGD77Manager)conv).print( outputFileAbsolutePath, outputLocalCdiId, originatorSoftwareName, originatorSoftwareVersion);
		default:
			throw new OctopusException(messages.getString("abstractcontroller.unrecognizedInputFormat"));
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
				LOGGER.error(messages.getString("abstractcontroller.unrecognizedInputFormat"));
			}
		} catch (Exception e) {
			LOGGER.error(messages.getString("errorClosingInputFile"));
		}

	}

}
