package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdn.vocabulary.implementations.CollectionFactory;
import sdn.vocabulary.interfaces.ICollectionFactory;
import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.medatlas.Medatlas2CFPointConverter;
import fr.ifremer.medatlas.exceptions.ConverterException;
import fr.ifremer.medatlas.exceptions.MedatlasReaderException;
import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.io.driver.DriverManager;
import fr.ifremer.octopus.io.driver.impl.CFPointDriverImpl;
import fr.ifremer.octopus.io.driver.impl.DriverManagerImpl;
import fr.ifremer.octopus.io.driver.impl.MedatlasSDNDriverImpl;
import fr.ifremer.octopus.io.driver.impl.OdvSDNDriverImpl;
import fr.ifremer.octopus.model.Conversion;
import fr.ifremer.octopus.model.Format;
import fr.ifremer.octopus.model.InputFileVisitor;
import fr.ifremer.octopus.model.OctopusModel;
import fr.ifremer.octopus.utils.SDNVocabs;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;

public abstract class AbstractController {

	private static final Logger LOGGER = LogManager.getLogger(AbstractController.class);

	protected static String CDI_SEPARATOR=",";


	private DriverManager driverManager = new DriverManagerImpl();
	protected OctopusModel model;
	/**
	 * Required conversion deduced from input and output formats
	 */
	private Conversion conversion;



	public AbstractController()  {
		this.driverManager.registerNewDriver(new MedatlasSDNDriverImpl());
		this.driverManager.registerNewDriver(new OdvSDNDriverImpl());
		this.driverManager.registerNewDriver(new CFPointDriverImpl());


	}


	public void init(File inputPath) throws IOException {
		Format inputFormat = getFirstFileInputFormat(inputPath);
		model = new OctopusModel(inputPath.getAbsolutePath());
		model.setInputFormat(inputFormat);
	}
	protected void abort(){
		LOGGER.info("abort"); // TODO msg
	}

	/**
	 * Process split and/or conversion
	 * @throws OctopusException 
	 * @throws VocabularyException 
	 */
	public void process() throws OctopusException {
		checkInputOutputFormatCompliance();

		if (model.getCdiList().isEmpty()){
			LOGGER.info("all CDIs exported");
		}

		try {
			File in = new File(model.getInputPath());
			if (in.isDirectory()){
				for (File f: in.listFiles())
					processFile (f);
			}else{
				processFile (in);
			}
		} catch (ConverterException e) {
			LOGGER.error(e.getMessage());
			throw new OctopusException(e);
		} catch (VocabularyException e) {
			LOGGER.error(e.getMessage());
			throw new OctopusException(e);
		} catch (MedatlasReaderException e) {
			LOGGER.error(e.getMessage());
			throw new OctopusException(e);
		}

	}

	private void processFile(File in) throws ConverterException, VocabularyException, MedatlasReaderException {
		String unitsTranslationFileName = "unitsTranslation.xml";

		
		// TODO Process cases
		if (! model.getCdiList().isEmpty()){
			// 	split
		}
		
		
		
		
		if (conversion==Conversion.MEDATLAS_SDN_TO_CF_POINT){
			createOutputDir();
			Medatlas2CFPointConverter conv = new Medatlas2CFPointConverter(" from Octopus", 
					SDNVocabs.getInstance().getCf(),
					unitsTranslationFileName);

			conv.processFile(in.getAbsolutePath(), model.getOutputPath(), model.isMono(), true);
		}

	}

	private void createOutputDir(){
		File out = new File(model.getOutputPath());
		out.mkdir();
	}
	/**
	 * 
	 * @return true if conversion is none or an available conversion type, false if conversion is not available
	 * @throws OctopusException 
	 */
	private void checkInputOutputFormatCompliance() throws OctopusException{
		if (model.getInputFormat().equals(model.getOutputFormat())){
			LOGGER.info("output and input formats are identical: no need to convert");
			conversion = Conversion.NONE;
		}else{

			switch (model.getInputFormat()) {
			case MEDATLAS_SDN:
				if (model.getOutputFormat().equals(Format.ODV_SDN)){
					conversion = Conversion.MEDATLAS_SDN_TO_ODV_SDN;
				}else if(model.getOutputFormat().equals(Format.CFPOINT)){
					conversion = Conversion.MEDATLAS_SDN_TO_CF_POINT;
				}
				break;
			case ODV_SDN:
				if(model.getOutputFormat().equals(Format.CFPOINT)){
					conversion = Conversion.ODV_SDN_TO_CFPOINT;
				}else if(model.getOutputFormat().equals(Format.MEDATLAS_SDN)){
					throw new OctopusException("format " + model.getInputFormat() + " can not be converted to " + model.getOutputFormat().getName());
				}
				break;
			case CFPOINT:
				throw new OctopusException("format " + model.getInputFormat() + " can not be converted to " + model.getOutputFormat().getName());
			default:
				throw new OctopusException("not implemented: " +  model.getInputFormat() + " to " + model.getOutputFormat().getName());
			}
		}

	}
	/**
	 * 
	 * @param file 
	 * @return the file format
	 * @throws IOException
	 */
	private Format getFormat(String file) throws IOException {
		Driver d = getDriver(file);
		if (d!=null){
			LOGGER.info("detected input format: "+d.getFormat().getName());
			return d.getFormat();
		}else{
			throw new IOException("unrecognized input format");
		}
	}
	private Driver getDriver(String file) throws IOException {
		return driverManager.findDriverForFile(file);
	}

	/**
	 * 
	 * @param inputPath
	 * @return if inputPath is a file, return the input file format; if input is a directory, return the format of the first found file
	 * @throws IOException
	 */
	protected Format getFirstFileInputFormat(File inputPath) throws IOException{
		InputFileVisitor fileProcessor = new InputFileVisitor();
		Files.walkFileTree(Paths.get(inputPath.getAbsolutePath()), fileProcessor);
		String firstFile = fileProcessor.getFirstFile();
		Format format = getFormat(firstFile);
		return format;

	}
}
