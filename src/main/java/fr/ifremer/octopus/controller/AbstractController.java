package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.medatlas.exceptions.ConverterException;
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
import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;
import fr.ifremer.octopus.utils.SDNVocabs;
import fr.ifremer.seadatanet.splitter.CFSplitter;
import fr.ifremer.seadatanet.splitter.SdnSplitter;
import fr.ifremer.seadatanet.splitter.SplitterException;

public abstract class AbstractController {



	private static final Logger LOGGER = LogManager.getLogger(AbstractController.class);

	private DriverManager driverManager = new DriverManagerImpl();
	protected OctopusModel model;


	/**
	 * Required conversion deduced from input and output formats
	 */
	private Conversion conversion;


	private File tmpDir;
	private String tmpPath = "OctopusTmpDirectory";



	public AbstractController()  {
		try {
			deleteTmp();
		} catch (IOException e) {
		}

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
	 */
	public void process() throws OctopusException {
		checkInputOutputFormatCompliance();

		if (conversion == Conversion.NONE){
			if (model.getCdiList().isEmpty()){
				if (model.getOutputType() == OUTPUT_TYPE.MONO ){
					LOGGER.info("split input file in n monostation files");
				}else{
					LOGGER.info("nothing to do: no cdi to be exported, no conversion");
					return;
				}
			}
		}else{
			if (model.getCdiList().isEmpty()){
				LOGGER.info("all CDIs exported");
			}
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
		}  catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage());
			throw new OctopusException(e);
		} catch (SplitterException e) {
			LOGGER.error(e.getMessage());
			throw new OctopusException(e);
		} finally{
			try {
				deleteTmp();
			} catch (IOException e) {
				LOGGER.error(e.getMessage());
			}
		}

		LOGGER.info("process ended successfully");

	}

	private void processFile(File in) 
			throws 
			ConverterException, 
			VocabularyException, 
			FileNotFoundException, 
			SplitterException,
			OctopusException {


		if (conversion==Conversion.NONE){
			if( model.getInputFormat()==Format.CFPOINT){
				// Cf2Cf
				processCf2Cf(in);
			}else{
				// Med2Med or Odv2Odv
				processSDNSplitter();
			}
		}else if (conversion==Conversion.MEDATLAS_SDN_TO_CF_POINT
				||conversion==Conversion.ODV_SDN_TO_CFPOINT
				){
			processX2Cf(in);
		}else if(conversion==Conversion.MEDATLAS_SDN_TO_ODV_SDN){
			processSDNSplitter();
		}

	}


	private void processSDNSplitter() throws SplitterException,
	VocabularyException {


		SdnSplitter splitter = new SdnSplitter(
				model.getInputPath(), 
				model.getOutputPath(),
				model.getOutputFormat().getName(), 
				model.getOutputType().toString(), 
				model.getCdiList().toArray(new String[model.getCdiList().size()]), 
				1L, 
				SDNVocabs.getInstance().getCf());

		splitter.split();
	}


	private void processX2Cf(File in)
			throws
			VocabularyException,
			SplitterException, 
			OctopusException {


		ConvertersManager convMgr;
		try {
			convMgr = new ConvertersManager(model.getInputFormat());
		} catch (fr.ifremer.seadatanet.odvsdn2cfpoint.exceptions.ConverterException | ConverterException | VocabularyException e) {
			throw new OctopusException(e.getMessage());
		}


		// X to CF -> X2CFPointConverter
		if (model.getCdiList().isEmpty()){

			// create output directory for mono and multi, as converters creates output files with input file names (changing extension)
			if (model.isMono()){
				createOutputDir();
				String convOutputPath = convMgr.processFile(in.getAbsolutePath(), model.getOutputPath(), model.isMono());
				LOGGER.info("output directory is ready: "+ convOutputPath);
			}else{
				// multi: rename file as asked to octopus (instead of converter name)
				createTmpDir();
				String convOutputPath = convMgr.processFile(in.getAbsolutePath(), tmpPath, model.isMono());
				try {
					FileUtils.moveFile(new File(convOutputPath), new File(model.getOutputPath()));
				} catch (IOException e) {
					throw new OctopusException(e.getMessage());
				}finally{
					try {
						deleteTmp();
					} catch (IOException e) {
						throw new OctopusException(e.getMessage());
					}
				}
				LOGGER.info("output file is ready: "+ model.getOutputPath());

			}


		}else{
			String tmpSplit="";
			// split before
			if (model.getOutputType()== OUTPUT_TYPE.MONO){
				createTmpDir();
				tmpSplit = tmpPath;
			}else{
				if (model.getInputFormat()==Format.ODV_SDN){
					//					tmpPath = model.getOutputPath()+".txt"; // splitter checks odv output extensions
					tmpSplit = model.getOutputPath()+"tmp.txt"; 
				}else{
					//					tmpPath = model.getOutputPath()+".med"; // splitter checks odv output extensions
					tmpSplit = model.getOutputPath()+"tmp.med"; 
				}
			}
			
			
			SdnSplitter splitter = new SdnSplitter(
					model.getInputPath(), 
					tmpSplit, 
					model.getInputFormat().getName(), // split in same format as input
					model.getOutputType().toString(), 
					model.getCdiList().toArray(new String[model.getCdiList().size()]), 
					1L, 
					SDNVocabs.getInstance().getCf());
			try{
				splitter.split();
			}catch(Exception e){
				LOGGER.error(e.getMessage());
			}

			
			
			if (model.getOutputType()== OUTPUT_TYPE.MONO){
				createOutputDir();
				for (File f : tmpDir.listFiles()){
					convMgr.processFile(f.getAbsolutePath(), model.getOutputPath(), model.isMono());
				}
				LOGGER.info("output directory is ready: "+ model.getOutputPath());
				try {
					deleteTmp();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				createTmpDir();
				String convOutputPath = convMgr.processFile(tmpSplit, tmpPath, model.isMono());
				
				try {
					FileUtils.moveFile(new File(convOutputPath), new File(model.getOutputPath()));
					new File(tmpSplit).delete();
				} catch (IOException e) {
					throw new OctopusException(e.getMessage());
				}finally{
					try {
						deleteTmp();
						new File(tmpSplit).delete();
					} catch (IOException e) {
						throw new OctopusException(e.getMessage());
					}
				}
				LOGGER.info("output file is ready: "+ model.getOutputPath());
			}

		}
	}

	private void processCf2Cf(File in) throws SplitterException,
	FileNotFoundException, VocabularyException {
		// 	CF to CF -> cfSplitter
		CFSplitter splitter = new CFSplitter(
				in.getAbsolutePath(), 
				model.getOutputPath(), 
				model.getOutputFormat().toString(), 
				model.getOutputType().toString(),
				model.getCdiList().toArray(new String[model.getCdiList().size()]), 
				1L, 
				SDNVocabs.getInstance().getCf());
		splitter.split();
	}

	private void createOutputDir(){
		File out = new File(model.getOutputPath());
		out.mkdir();
	}
	private void createTmpDir(){
		tmpDir = new File(tmpPath);
		tmpDir.mkdir();
	}
	private void deleteTmp() throws IOException{
		File tmp = new File(tmpPath);
		if (tmp.isFile()){
			tmp.delete();
		}else{
			FileUtils.deleteDirectory(new File(tmpPath));
		}
	}

	/**
	 * 
	 * @return true if conversion is none or an available conversion type, false if conversion is not available
	 * @throws OctopusException 
	 */
	private void checkInputOutputFormatCompliance() throws OctopusException{


		if (model.getInputFormat().equals(model.getOutputFormat())){
			LOGGER.info("output and input formats are identical");
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

	public OctopusModel getModel() {
		return model;
	}
}
