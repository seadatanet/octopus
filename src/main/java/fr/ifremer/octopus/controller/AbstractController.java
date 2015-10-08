package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.checker.FormatChecker;
import fr.ifremer.octopus.controller.checker.MedatlasFormatChecker;
import fr.ifremer.octopus.controller.checker.OdvFormatChecker;
import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.io.driver.DriverManager;
import fr.ifremer.octopus.io.driver.impl.CFPointDriverImpl;
import fr.ifremer.octopus.io.driver.impl.DriverManagerImpl;
import fr.ifremer.octopus.io.driver.impl.MedatlasSDNDriverImpl;
import fr.ifremer.octopus.io.driver.impl.OdvSDNDriverImpl;
import fr.ifremer.octopus.model.Conversion;
import fr.ifremer.octopus.model.InputFileVisitor;
import fr.ifremer.octopus.model.OctopusModel;
import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;
import fr.ifremer.octopus.view.CdiListManager;
import fr.ifremer.sismer_tools.seadatanet.Format;

public abstract class AbstractController {



	private static final Logger LOGGER = LogManager.getLogger(AbstractController.class);

	private DriverManager driverManager = new DriverManagerImpl();
	private CdiListManager cdiListManager;
	protected OctopusModel model;
	/**
	 * Required conversion deduced from input and output formats
	 */
	private Conversion conversion;

	/**
	 * 
	 */
	public AbstractController()  {

		this.driverManager.registerNewDriver(new MedatlasSDNDriverImpl());
		this.driverManager.registerNewDriver(new OdvSDNDriverImpl());
		this.driverManager.registerNewDriver(new CFPointDriverImpl());
	}

	/**
	 * 
	 * @param inputPath
	 * @throws IOException
	 */
	public void init(File inputPath) throws IOException {
		Format inputFormat = getFirstFileInputFormat(inputPath);
		model = new OctopusModel(inputPath.getAbsolutePath());
		model.setInputFormat(inputFormat);

		createCdiManager();
	}
	public void createCdiManager(){
		cdiListManager = new CdiListManager(this);
	}

	public CdiListManager getCdiListManager() {
		return cdiListManager;
	}

	/**
	 * 
	 */
	protected void abort(){
		LOGGER.info("abort"); // TODO msg
	}

	/**
	 * Process split and/or conversion
	 * @return list of output files paths
	 * @throws OctopusException
	 */
	public List<String> process() throws OctopusException {

		List<String> outputFilesList=new ArrayList<String>();

		checkInputOutputFormatCompliance();
		checkOutputNameCompliance();

		if (conversion == Conversion.NONE){
			if (model.getCdiList().isEmpty()){
				if (model.getOutputType() == OUTPUT_TYPE.MONO ){
					LOGGER.info("split input file in n monostation files");
				}else{
					LOGGER.info("nothing to do: no cdi to be exported, no conversion");
					return outputFilesList;
				}
			}
		}else{
			if (model.getCdiList().isEmpty()){
				LOGGER.info("all CDIs exported");
			}
		}


		try {
			if (model.isMono() || model.getInputFile().isDirectory()){
				createOutputDir();
			}
			if (model.getInputFile().isDirectory()){
				for (File f: model.getInputFile().listFiles())
					outputFilesList=processFile (f, outputFilesList);
			}else{
				outputFilesList=processFile (model.getInputFile(), outputFilesList);
			}
		} 
		catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new OctopusException(e);
		} finally{
		}
		if (outputFilesList.isEmpty()){
			deleteOutputDir();
		}
		return outputFilesList;


	}

	private  void deleteOutputDir(){
		try {
			File out = new File(model.getOutputPath());
			FileUtils.deleteDirectory(out);
		} catch (IOException e) {
			LOGGER.error("error on output directory deletion"); // TODO
		}
	}
	/**
	 * 
	 * @param in
	 * @param outputFilesList
	 * @return list of output files paths
	 * @throws OctopusException
	 */
	private  List<String> processFile(File in, List<String> outputFilesList) throws OctopusException {
		ConvertersManager manager;
		try {
			manager = new ConvertersManager(in, model.getInputFormat());
		} catch (OctopusException e1) {
			LOGGER.error(e1.getMessage());
			throw new OctopusException("error on input file");
		}
		List<String> cdiToPrint;
		try {
			cdiToPrint = getCdiList(manager, in.getAbsolutePath());
		} catch (OctopusException e1) {
			return outputFilesList;
		}
		try {
			String out;


			// MONO
			if (model.isMono()){
				// Process
				for (String cdi: cdiToPrint){
					if (model.getInputFile().isDirectory()){
						createOutputSubDir(in.getName());
						out = getOutFilePath(in.getName(), cdi);
					}else{
						out = getOutFilePath(null, cdi);
					}

					manager.print(getOneCdiAsList(cdi),out, model.getOutputFormat());
					outputFilesList.add(out);
				}
			}
			//MULTI
			else{
				if (model.getInputFile().isDirectory()){
					createOutputSubDir(in.getName());
					out = getOutFilePath(in.getName(), null);
				}else{
					out = getOutFilePath(null, null);
				}
				// Process
				manager.print(cdiToPrint, out, model.getOutputFormat());
				outputFilesList.add(out);
			}


			manager.close();
		} catch (Exception e) {
			LOGGER.error("error on file "+ in.getAbsolutePath());
			LOGGER.error(e.getMessage());
			throw new OctopusException("error on input file");
		}


		return outputFilesList;

	}


	private String getOutFilePath(String in, String cdi){
		String outPath;
		String extension = "."+model.getOutputFormat().getOutExtension();
		// DIRECTORY
		if (model.getInputFile().isDirectory()){
			if (model.isMono()){
				outPath = model.getOutputPath()+File.separator+ in +File.separator+cdi+extension;
			}else{
				outPath = model.getOutputPath()+ in +extension;
			}
		}
		// FILE
		else{
			if (model.isMono()){
				outPath = model.getOutputPath()+File.separator+cdi+extension;
			}else{
				outPath = model.getOutputPath();
			}
		}
		return outPath;
	}


	private List<String> getOneCdiAsList(String cdi){
		List<String> oneCdi=new ArrayList<String>();
		oneCdi.add(cdi);
		return oneCdi;
	}
	private List<String> getCdiList(ConvertersManager mgr, String filePath ) throws OctopusException{
		List<String>cdiL = new ArrayList<>();
		if (model.getCdiList().isEmpty()){
			cdiL = mgr.getInputFileCdiIdList();
		}else{
			for (String cdi: model.getCdiList()){
				if (mgr.containsCdi(cdi)){
					cdiL.add(cdi);
				}else{
					LOGGER.warn("local CDI ID "+cdi+" has not been found in "+filePath);
				}
			}
			if(cdiL.isEmpty()){
				throw new OctopusException("None of the local CDI ID has been found in file " + filePath);// TODO
			}
		}
		return cdiL;
	}

	private void createOutputDir() throws OctopusException{
		boolean ok=true;
		File out = new File(model.getOutputPath());
		if (!out.exists()){
			ok = out.mkdir();
		}
		if (!ok){
			Path p = Paths.get(model.getOutputPath());

			if (!p.getParent().toFile().exists()){
				LOGGER.error("directory "+ p.getParent() + " does not exist");// TODO
			}
			throw new OctopusException("unable to create output directory "+ model.getOutputPath());// TODO
		}
	}
	private void createOutputSubDir(String in){
		File out = new File(model.getOutputPath()+File.separator+in);
		out.mkdir();
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

	protected void checkOutputNameCompliance() throws OctopusException{
		if(new File (model.getInputPath()).isFile()
				&& model.getOutputType()==OUTPUT_TYPE.MULTI 
				&& !model.getOutputFormat().isExtensionCompliant(model.getOutputPath())){
			throw new OctopusException("output file extension is not valid:  "+model.getOutputFormat().getName() 
					+ " files must use \"." +model.getOutputFormat().getMandatoryExtension()+ "\" extension." );

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

	/**
	 * @throws Exception 
	 * 
	 */
	public void checkFormat() throws Exception {

		FormatChecker checker = getFormatChecker();
		if (checker==null){
			throw new OctopusException("No checker for format "+model.getInputFormat().getName()+" implemented yet");// TODO
		}
		File in = new File(model.getInputPath());
		if (in.isDirectory()){
			for (File f: in.listFiles())
				checker.check (f);
		}else{
			checker.check (in);
		}
	}

	/**
	 * 
	 * @return
	 * @throws OctopusException 
	 */
	private FormatChecker getFormatChecker() throws OctopusException{
		FormatChecker checker = null;
		switch (model.getInputFormat()) {
		case MEDATLAS_SDN:
			checker= new MedatlasFormatChecker();
		case ODV_SDN:
			checker= new OdvFormatChecker();
		default:
			break;
		}
		return checker;
	}

	/**
	 * 
	 * @return
	 */
	public OctopusModel getModel() {
		return model;
	}
}
