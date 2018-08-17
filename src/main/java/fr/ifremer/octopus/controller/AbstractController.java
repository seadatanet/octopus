package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.checker.CFPointFormatChecker;
import fr.ifremer.octopus.controller.checker.FormatChecker;
import fr.ifremer.octopus.controller.checker.MedatlasFormatChecker;
import fr.ifremer.octopus.controller.checker.OdvFormatChecker;
import fr.ifremer.octopus.controller.couplingTable.CouplingTableManager;
import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.io.driver.DriverManager;
import fr.ifremer.octopus.io.driver.impl.CFPointDriverImpl;
import fr.ifremer.octopus.io.driver.impl.DriverManagerImpl;
import fr.ifremer.octopus.io.driver.impl.MGDDriverImpl;
import fr.ifremer.octopus.io.driver.impl.MedatlasSDNDriverImpl;
import fr.ifremer.octopus.io.driver.impl.OdvSDNDriverImpl;
import fr.ifremer.octopus.model.Conversion;
import fr.ifremer.octopus.model.InputFileVisitor;
import fr.ifremer.octopus.model.OctopusModel;
import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.octopus.view.CdiListManager;
import fr.ifremer.sismer_tools.coupling.CouplingRecord;
import fr.ifremer.sismer_tools.seadatanet.Format;

public abstract class AbstractController {



	private static final Logger LOGGER = LogManager.getLogger(AbstractController.class);
	protected static final String GLOBAL_BATCH_PREFIX="[GLOBAL BATCH] ";
	protected static final String GLOBAL_BATCH_SUCCESS=GLOBAL_BATCH_PREFIX + "success";
	protected static final String GLOBAL_BATCH_ARGS=GLOBAL_BATCH_PREFIX + "args";
	protected static final String GLOBAL_BATCH_ERROR=GLOBAL_BATCH_PREFIX + "error";
	private static final String FILE_BATCH_PREFIX="[FILE BATCH] ";
	private static final String FILE_BATCH_SUCCESS=FILE_BATCH_PREFIX + "success";
	private static final String FILE_BATCH_ARGS=FILE_BATCH_PREFIX + "args";
	private static final String FILE_BATCH_ERROR=FILE_BATCH_PREFIX + "error";
	

	private DriverManager driverManager = new DriverManagerImpl();
	private CdiListManager cdiListManager;
	protected OctopusModel model;
	private PreferencesManager prefsMgr;
	/**
	 * Required conversion deduced from input and output formats
	 */
	private Conversion conversion;
	protected ResourceBundle messages ;
	protected int nbFilesInInputDirectory;
	protected int nbErrorFilesInInputDirectory;
	/**
	 * @throws OctopusException 
	 * 
	 */
	public AbstractController() throws OctopusException  {
		
		this.logStart();
		
		
		this.driverManager.registerNewDriver(new MedatlasSDNDriverImpl());
		this.driverManager.registerNewDriver(new OdvSDNDriverImpl());
		this.driverManager.registerNewDriver(new CFPointDriverImpl());
		this.driverManager.registerNewDriver(new MGDDriverImpl());

		// load preferences from XMl file
		prefsMgr = PreferencesManager.getInstance();
		prefsMgr.load();
		LOGGER.info("LANGUAGE: "+prefsMgr.getLocale());
		
		
		messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());

	}

	protected abstract void logStart() ;

	/**
	 * 
	 * @param inputPath
	 * @throws IOException
	 */
	public void init(String inputPath) throws IOException {
		Format inputFormat = getFirstFileInputFormat(new File(inputPath));
		model = new OctopusModel(inputPath);
		model.setInputFormat(inputFormat);
		//32965
		if (inputFormat.equals(Format.MGD_81) || inputFormat.equals(Format.MGD_98) ){
			model.setOutputType(OUTPUT_TYPE.MONO);
		}

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
		LOGGER.info(messages.getString("abstractcontroller.abort")); 
	}

	/**
	 * Process split and/or conversion
	 * @param jsonlogger 
	 * @return list of output files paths
	 * @throws OctopusException
	 * @throws SQLException 
	 */
	public List<String> processConversion(Logger jsonLogger) throws OctopusException, SQLException {
		
		// used only if input is a directory, to store info on eventual errors on some files
		nbFilesInInputDirectory=0;
		nbErrorFilesInInputDirectory=0;
		if(model.getInputFile().isDirectory()){
			LOGGER.info(MessageFormat.format(messages.getString("abstractcontroller.inputDirectoryNbFiles"), model.getInputFile().listFiles().length));
		}
		
		List<String> outputFilesList=new ArrayList<String>();

		try {
			if (PreferencesManager.getInstance().isCouplingEnabled()){
				if (!CouplingTableManager.getInstance().checkPrefixCompliance(model.getOutputPath())){
					throw new OctopusException(messages.getString("abstractcontroller.couplingPrefixNotCompliant"));
				}
			}
		} catch (ClassNotFoundException e1) {
			throw new OctopusException(e1.getMessage());
		} catch (SQLException e1) {
			throw e1;
		}

		checkInputOutputFormatCompliance();
		checkOutputNameCompliance();
		
		if (conversion == Conversion.NONE){
			if (model.getCdiList().isEmpty()){
				if (model.getOutputType() == OUTPUT_TYPE.MONO ){
					LOGGER.info(messages.getString("abstractcontroller.splitMono"));
					
				}else{
					LOGGER.info(messages.getString("abstractcontroller.outputIdentical"));
				}
			}
		}else{
			if (model.getInputFormat()==Format.MGD_81 ||model.getInputFormat()==Format.MGD_98){
//				model.setOutputType(OUTPUT_TYPE.MONO);//32965
			}else if(model.getCdiList().isEmpty()){
				LOGGER.info(messages.getString("abstractcontroller.allCDIExported"));
			}
		}

		try {
			if (model.isMono() || model.getInputFile().isDirectory()){
				createOutputDir();
			}
			if (model.getInputFile().isDirectory()){
				for (File f: model.getInputFile().listFiles()){
					nbFilesInInputDirectory+=1;
					try{
						outputFilesList=processFile (f, outputFilesList, jsonLogger);
					}catch(Exception e){
						LOGGER.error(e.getMessage());
						LOGGER.error(MessageFormat.format(messages.getString("abstractcontroller.errorOnOneFileInADirectory"), f.getAbsolutePath()));
						nbErrorFilesInInputDirectory+=1;
					}
				}
			}else{
				outputFilesList=processFile (model.getInputFile(), outputFilesList, jsonLogger);
			}
		} 
		catch (Exception e) {
			LOGGER.error(e.getMessage());
			throw new OctopusException(e);
		} finally{
			try {
				if (PreferencesManager.getInstance().isCouplingEnabled()){
					CouplingTableManager.getInstance().closeConnection();
				}
			} catch (ClassNotFoundException e) {
				LOGGER.error("error closing coupling table connection "+ e.getMessage());
			}
		}
		if (outputFilesList.isEmpty()){
			deleteOutputDir();
		}


		/**
		 * for debug only
		 */
		//		try {
		//			List<CouplingRecord> list = CouplingTableManager.getInstance().list();
		//			for (CouplingRecord cr : list){
		//				LOGGER.info(cr.toString());
		//			}
		//		} catch (ClassNotFoundException | SQLException e) {
		//			e.printStackTrace();
		//		}
		
		/**
		 * INFOS AFTER PROCESS
		 */
		if(model.getInputFile().isDirectory()){
			// some files on error
			if(nbErrorFilesInInputDirectory>0 && nbFilesInInputDirectory>nbErrorFilesInInputDirectory){
				LOGGER.warn(MessageFormat.format(messages.getString("abstractcontroller.processDirWarnNBFiles"), nbErrorFilesInInputDirectory, nbFilesInInputDirectory));
			}
			// all files on error
			else if(nbErrorFilesInInputDirectory==nbFilesInInputDirectory){
				LOGGER.error(MessageFormat.format(messages.getString("abstractcontroller.processDirErrorNBFiles"), nbErrorFilesInInputDirectory));
			}
			// all files ok
			else{
				LOGGER.info(MessageFormat.format(messages.getString("abstractcontroller.processSucessNBFiles"), nbFilesInInputDirectory, outputFilesList.size()));
				LOGGER.info(outputFilesList);
			}

		}else{
			if (outputFilesList.size()>0){
				LOGGER.info(MessageFormat.format(messages.getString("abstractcontroller.processSucessNBFiles"), 1, outputFilesList.size()));
				LOGGER.info(outputFilesList);
			}else{
				LOGGER.error("no file exported");// should not append because an exception should have been raised
			}
		}
		
		
		return outputFilesList;


	}
	

	private  void deleteOutputDir(){
		try {
			File out = new File(model.getOutputPath());
			FileUtils.deleteDirectory(out);
		} catch (IOException e) {
			LOGGER.error(messages.getString("abstractcontroller.errorOnOutputDirDeletion"));
		}
	}
	/**
	 * 
	 * @param in
	 * @param outputFilesList
	 * @param jsonLogger 
	 * @return list of output files paths
	 * @throws OctopusException
	 */
	private  List<String> processFile(File in, List<String> outputFilesList, Logger jsonLogger) throws OctopusException {
		HashMap<String, Object> jsonRes = new HashMap<>();
		LOGGER.info("process file: " +  in.getName());
		ConvertersManager manager;
		try {
			manager = new ConvertersManager(in, model.getInputFormat());
		} catch (OctopusException e1) {
			LOGGER.error(e1.getMessage());
			if (jsonLogger!=null){
				jsonRes.put(FILE_BATCH_SUCCESS, false);
				jsonRes.put(FILE_BATCH_ARGS,in.getName());
				jsonRes.put(FILE_BATCH_ERROR, e1);
				jsonLogger.info(jsonRes);
			}
			
			throw new OctopusException("error on input file");
		}
		List<String> cdiToPrint;
		try {
			cdiToPrint = getCdiList(manager, in.getAbsolutePath());
		} catch (OctopusException e1) {
			LOGGER.warn(e1.getMessage());
			return outputFilesList;
		}
		
		try {
			String out;


			// MONO
			if (model.isMono()){
				// Process
				if (model.getInputFormat().equals(Format.MGD_81) ||model.getInputFormat().equals(Format.MGD_98) ){
//					createOutputSubDir(in.getName());
					String extension = "."+model.getOutputFormat().getOutExtension();
					out=model.getOutputPath()+File.separator+getOutputCDI(in.getName())+extension;
					List<CouplingRecord> records = manager.print(null,out, model.getOutputFormat(),  getOutputCDI(in.getName()));
					if (PreferencesManager.getInstance().isCouplingEnabled()){
						CouplingTableManager.getInstance().add(records);			
					}
					outputFilesList.add(out);
					
				}else{
				for (String cdi: cdiToPrint){
					if (model.getInputFile().isDirectory()){
						createOutputSubDir(in.getName());
						out = getOutFilePath(getInpufNameWithoutExtension(in.getName()), cdi);
					}else{
						out = getOutFilePath(null, cdi);
					}

					List<CouplingRecord> records = manager.print(getOneCdiAsList(cdi),out, model.getOutputFormat(), null);
					if (PreferencesManager.getInstance().isCouplingEnabled()){
						CouplingTableManager.getInstance().add(records);			
					}
					outputFilesList.add(out);
				}
				}
			}
			//MULTI
			else{
//				if (model.getInputFormat().equals(Format.MGD_81)||model.getInputFormat().equals(Format.MGD_98)){
//					String extension = "."+model.getOutputFormat().getOutExtension();
//					out=model.getOutputPath()+File.separator+getOutputCDI(in.getName())+extension;
//				}else
					if (model.getInputFile().isDirectory()){
					out = getOutFilePath(getInpufNameWithoutExtension(in.getName()), null);
				}else{
					out = getOutFilePath(null, null);
				}
				
				// Process
					List<CouplingRecord> records = manager.print(cdiToPrint, out, model.getOutputFormat(), getOutputCDI(in.getName()));
				if (PreferencesManager.getInstance().isCouplingEnabled()){
					CouplingTableManager.getInstance().add(records);		
				}
				outputFilesList.add(out);
			}


			manager.close();
		} catch (Exception e) {
			LOGGER.error("error on file "+ in.getAbsolutePath());
			LOGGER.error(e.getMessage());
			
			if (jsonLogger!=null){
				jsonRes.put(FILE_BATCH_SUCCESS, false);
				jsonRes.put(FILE_BATCH_ARGS,in.getName());
				jsonRes.put(FILE_BATCH_ERROR, e);
				jsonLogger.info(jsonRes);
			}
			
			
			throw new OctopusException("error processing file "+in.getAbsolutePath());
		}


		return outputFilesList;

	}
	/**
	 * 
	 * @param inputFileName
	 * @return local_cdi_id to be used for inputFileName, if input format is MGD
	 * @throws OctopusException
	 */
	private String getOutputCDI(String inputFileName) throws OctopusException{
		if (model.getInputFormat()==Format.MGD_81||model.getInputFormat()==Format.MGD_98){
			if (model.getInputFile().isDirectory()){
				return  model.getOuputLocalCdiId(inputFileName);
			}else{
				return  model.getOuputLocalCdiId();
			}}else{
				return null;
			}
	}
	private String getOutFilePath(String in, String cdi){
		String outPath;
		String extension = "."+model.getOutputFormat().getOutExtension();
		// DIRECTORY
		if (model.getInputFile().isDirectory()){
			if (model.isMono()){
				outPath = model.getOutputPath()+File.separator+ in +File.separator+cdi+extension;
			}else{
				outPath = model.getOutputPath()+File.separator+ in +extension;
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
					// log only in batch mode (in GUI mode, user can not ask for non existing CDI)
					if (this instanceof BatchController){
						LOGGER.warn(MessageFormat.format(messages.getString("abstractcontroller.cdiNotFound"), cdi, filePath));
					}
				}
			}
			if(cdiL.isEmpty()){
				throw new OctopusException(MessageFormat.format(messages.getString("abstractcontroller.noCdiFound"),  filePath));
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
				LOGGER.error(MessageFormat.format(messages.getString("abstractcontroller.directoryDoesNotExist"),  p.getParent()));
			}
				throw new OctopusException(MessageFormat.format(messages.getString("abstractcontroller.errorOnOutputDirCreation"), model.getOutputPath()));
			}
	}
	
	private String getInpufNameWithoutExtension(String in){
		int dotIndex = in.lastIndexOf(".");
		if (dotIndex==-1){
			 return in;
		}else{
			return in.substring(0, dotIndex);
		}
	}
	private void createOutputSubDir(String in){
		File out ;
		out = new File(model.getOutputPath()+File.separator+getInpufNameWithoutExtension(in));
		out.mkdir();
	}


	/**
	 * 
	 * @return true if conversion is none or an available conversion type, false if conversion is not available
	 * @throws OctopusException 
	 */
	private void checkInputOutputFormatCompliance() throws OctopusException{


		if (model.getInputFormat().equals(model.getOutputFormat())){
			LOGGER.info(messages.getString("abstractcontroller.formatsIdentical"));
			conversion = Conversion.NONE;

		}else{

			switch (model.getInputFormat()) {
			case MEDATLAS_SDN:
			case MEDATLAS_NON_SDN:
				if (model.getOutputFormat().equals(Format.MEDATLAS_SDN)){
					conversion = Conversion.NONE;
				}else if (model.getOutputFormat().equals(Format.ODV_SDN)){
					conversion = Conversion.MEDATLAS_SDN_TO_ODV_SDN;
				}else if(model.getOutputFormat().equals(Format.CFPOINT)){
					conversion = Conversion.MEDATLAS_SDN_TO_CF_POINT;
				}
				break;
			case ODV_SDN:
				if(model.getOutputFormat().equals(Format.CFPOINT)){
					conversion = Conversion.ODV_SDN_TO_CFPOINT;
				}else if(model.getOutputFormat().equals(Format.MEDATLAS_SDN)){
					throw new OctopusException(
							MessageFormat.format(messages.getString("abstractcontroller.canNotConvertFromTo"),
									model.getInputFormat().getName(), 
									model.getOutputFormat().getName())
					);
					
				}
				break;
			case CFPOINT:
				if (model.getOutputFormat().equals(Format.ODV_SDN)){
					conversion = Conversion.CFPOINT_TO_ODV_SDN;
				}else
				throw new OctopusException(
						MessageFormat.format(messages.getString("abstractcontroller.canNotConvertFromTo"),
								model.getInputFormat().getName(), 
								model.getOutputFormat().getName())
				);
				break;
			case MGD_81:
			case MGD_98:
				if (model.getOutputFormat().equals(Format.ODV_SDN)){
					conversion = Conversion.MGD_TO_ODV;
				}else{
					throw new OctopusException(
							MessageFormat.format(messages.getString("abstractcontroller.canNotConvertFromTo"),
									model.getInputFormat().getName(), 
									model.getOutputFormat().getName())
					);
				}
				break;
			default:
				throw new OctopusException(
						MessageFormat.format(messages.getString("abstractcontroller.conversionNotImplemented"),
								model.getInputFormat().getName(), 
								model.getOutputFormat().getName())
						);
			}
		}

	}

	protected void checkOutputNameCompliance() throws OctopusException{
		if(new File (model.getInputPath()).isFile()
				&& model.getOutputType()==OUTPUT_TYPE.MULTI 
				&& !model.getOutputFormat().isExtensionCompliant(model.getOutputPath())){
			throw new OctopusException(
					MessageFormat.format(messages.getString("abstractcontroller.invalidOutputExtension"),
							model.getOutputFormat().getName() , 
							model.getOutputFormat().getMandatoryExtension())
					);

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
			LOGGER.info(
					MessageFormat.format(messages.getString("abstractcontroller.detectedInputFormat"),
							d.getFormat().getName() )
					);
			return d.getFormat();
		}else{
			throw new IOException(messages.getString("abstractcontroller.unrecognizedInputFormat"));
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
//		Files.walkFileTree(Paths.get(inputPath.getAbsolutePath()), fileProcessor);
		// depth = 1, because we do not read sub directories (and causes errors with svn)
		Files.walkFileTree(Paths.get(inputPath.getAbsolutePath()),  EnumSet.noneOf(FileVisitOption.class), 1, fileProcessor);
		String firstFile = fileProcessor.getFirstFile();
		Format format = getFormat(firstFile);
		return format;

	}

	/**
	 * 
	 * @param jsonlogger 
	 * @return true if succcess
	 */
	public boolean checkFormat(Logger jsonLogger)  {
		
		HashMap<String, Object> jsonRes = new HashMap<>();
		boolean result=true;
		FormatChecker checker = getFormatChecker();
		if (checker==null){
			LOGGER.warn(
					MessageFormat.format(messages.getString("abstractcontroller.checkerNotImmplemented"),
							model.getInputFormat().getName() )
					);
			return false;
		}
		File in = new File(model.getInputPath());
		Format inputFormat = null; 
		if (in.isDirectory()){
			int errors=0;
			for (File f: in.listFiles()){
				LOGGER.info("check file: "+ f.getName());
				try{
					// TODO do not check dirs  -> recursive
					if (inputFormat == null){
						inputFormat = checker.check (f);
					}else{
						checker.check (f);
					}
					if (jsonLogger!=null){
						jsonRes.put(FILE_BATCH_SUCCESS, true);
						jsonRes.put(FILE_BATCH_ARGS,f.getName());
						jsonRes.put(FILE_BATCH_ERROR, "");
						jsonLogger.info(jsonRes);
					}
				}catch(Exception e){
					if (jsonLogger!=null){
						jsonRes.put(FILE_BATCH_SUCCESS, false);
						jsonRes.put(FILE_BATCH_ARGS,f.getName());
						jsonRes.put(FILE_BATCH_ERROR, e);
						jsonLogger.info(jsonRes);
					}
					errors++;
					LOGGER.error(MessageFormat.format(messages.getString("abstractcontroller.invalidFile"),
							 f.getAbsolutePath()));
					result=false;
				}
			}
			if (errors==0){
//				LOGGER.info(messages.getString("abstractcontroller.allFilesValid"));
				LOGGER.info(MessageFormat.format(messages.getString("abstractcontroller.allFilesValid"), inputFormat.name()));
				
			}else{
				LOGGER.error( 
						MessageFormat.format(messages.getString("abstractcontroller.XInvalidFilesOnY"),
								errors, in.listFiles().length)
						);
				result=false;
			}
		}else{
			try{
				LOGGER.info("check file: "+ in.getName());
				inputFormat = checker.check (in);
				LOGGER.info(MessageFormat.format(messages.getString("abstractcontroller.formatIsValid"), inputFormat.getName()));
				if (jsonLogger!=null){
					jsonRes.put(FILE_BATCH_SUCCESS, true);
					jsonRes.put(FILE_BATCH_ARGS,in.getName());
					jsonRes.put(FILE_BATCH_ERROR, "");
					jsonLogger.info(jsonRes);
				}
			}catch(Exception e){
				LOGGER.error(
						MessageFormat.format(messages.getString("abstractcontroller.invalidFile"),
								in.getAbsolutePath())
						);
				if (jsonLogger!=null){
					jsonRes.put(FILE_BATCH_SUCCESS, false);
					jsonRes.put(FILE_BATCH_ARGS,in.getName());
					jsonRes.put(FILE_BATCH_ERROR, e);
					jsonLogger.info(jsonRes);
				}
				result=false;
			}

		}
		return result;
	}

	/**
	 * 
	 * @return
	 * @throws OctopusException 
	 */
	private FormatChecker getFormatChecker() {
		FormatChecker checker = null;
		switch (model.getInputFormat()) {
		case MEDATLAS_SDN:
		case MEDATLAS_NON_SDN:
			checker= new MedatlasFormatChecker();
			break;
		case ODV_SDN:
			checker= new OdvFormatChecker();
			break;
		case CFPOINT:
			checker= new CFPointFormatChecker();
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
