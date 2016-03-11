package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	private DriverManager driverManager = new DriverManagerImpl();
	private CdiListManager cdiListManager;
	protected OctopusModel model;
	private PreferencesManager prefsMgr;
	/**
	 * Required conversion deduced from input and output formats
	 */
	private Conversion conversion;
	protected ResourceBundle messages ;
	/**
	 * @throws OctopusException 
	 * 
	 */
	public AbstractController() throws OctopusException  {
		
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

	/**
	 * 
	 * @param inputPath
	 * @throws IOException
	 */
	public void init(String inputPath) throws IOException {
		Format inputFormat = getFirstFileInputFormat(new File(inputPath));
		model = new OctopusModel(inputPath);
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
		LOGGER.info(messages.getString("abstractcontroller.abort")); 
	}

	/**
	 * Process split and/or conversion
	 * @return list of output files paths
	 * @throws OctopusException
	 * @throws SQLException 
	 */
	public List<String> process() throws OctopusException, SQLException {

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
				
			}else if(model.getCdiList().isEmpty()){
				LOGGER.info(messages.getString("abstractcontroller.allCDIExported"));
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

					List<CouplingRecord> records = manager.print(getOneCdiAsList(cdi),out, model.getOutputFormat(), null);
					CouplingTableManager.getInstance().add(records);					
					outputFilesList.add(out);
				}
			}
			//MULTI
			else{
				if (model.getInputFile().isDirectory()){
					out = getOutFilePath(in.getName(), null);
				}else{
					out = getOutFilePath(null, null);
				}
				// Process
				List<CouplingRecord> records = manager.print(cdiToPrint, out, model.getOutputFormat(), getOutputCDI(in.getName()));
				CouplingTableManager.getInstance().add(records);		
				outputFilesList.add(out);
			}


			manager.close();
		} catch (Exception e) {
			LOGGER.error("error on file "+ in.getAbsolutePath());
			LOGGER.error(e.getMessage());
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
	private void createOutputSubDir(String in){
		File out ;
		// remove extension
		int dotIndex = in.lastIndexOf(".");
		if (dotIndex==-1){
			 out = new File(model.getOutputPath()+File.separator+in);
		}else{
			 out = new File(model.getOutputPath()+File.separator+in.substring(0, dotIndex));
		}
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
					throw new OctopusException(
							MessageFormat.format(messages.getString("abstractcontroller.canNotConvertFromTo"),
									model.getInputFormat().getName(), 
									model.getOutputFormat().getName())
					);
					
				}
				break;
			case CFPOINT:
				throw new OctopusException(
						MessageFormat.format(messages.getString("abstractcontroller.canNotConvertFromTo"),
								model.getInputFormat().getName(), 
								model.getOutputFormat().getName())
				);
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
		Files.walkFileTree(Paths.get(inputPath.getAbsolutePath()), fileProcessor);
		String firstFile = fileProcessor.getFirstFile();
		Format format = getFormat(firstFile);
		return format;

	}

	/**
	 * @throws Exception 
	 * 
	 */
	public void checkFormat()  {

		FormatChecker checker = getFormatChecker();
		if (checker==null){
			LOGGER.warn(
					MessageFormat.format(messages.getString("abstractcontroller.checkerNotImmplemented"),
							model.getInputFormat().getName() )
					);
			return;
		}
		File in = new File(model.getInputPath());
		if (in.isDirectory()){
			int errors=0;
			for (File f: in.listFiles()){
				try{
					// TODO do not check dirs  -> recursive
					checker.check (f);
				}catch(Exception e){
					errors++;
					LOGGER.error(MessageFormat.format(messages.getString("abstractcontroller.invalidFile"),
							 f.getAbsolutePath()));
				}
			}
			if (errors==0){
				LOGGER.info(messages.getString("abstractcontroller.allFilesValid"));
			}else{
				LOGGER.error( 
						MessageFormat.format(messages.getString("abstractcontroller.XInvalidFilesOnY"),
								errors, in.listFiles().length)
						);
			}
		}else{
			try{
				checker.check (in);
				LOGGER.info(messages.getString("abstractcontroller.formatIsValid"));
			}catch(Exception e){
				LOGGER.error(
						MessageFormat.format(messages.getString("abstractcontroller.invalidFile"),
								in.getAbsolutePath())
						);
			}

		}
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
			checker= new MedatlasFormatChecker();
			break;
		case ODV_SDN:
			checker= new OdvFormatChecker();
			break;
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
