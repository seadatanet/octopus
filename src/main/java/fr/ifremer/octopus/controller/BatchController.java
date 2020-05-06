package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.OctopusVersion;
import fr.ifremer.octopus.model.OctopusModel;
import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;
import fr.ifremer.octopus.utils.EdmoManager;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
import fr.ifremer.sismer_tools.seadatanet.Format;
import sdn.vocabulary.interfaces.VocabularyException;

public class BatchController extends AbstractController{
	private static final Logger LOGGER = LogManager.getLogger(BatchController.class);
	private static final Logger jsonLogger = LogManager.getLogger("JSON_FILE_APPENDER");
	private boolean isJunitTest ;
	private boolean checkOnly;
	private boolean update;

	private Options options;
	private CommandLineParser parser ;
	private HelpFormatter formatter;

	private static String OPTION_I = "i";
	private static String OPTION_O = "o";
	private static String OPTION_CHECKONLY = "check";
	private static String OPTION_F = "f";
	private static String OPTION_T = "t";
	private static String OPTION_CDI = "c";
	private static String OPTION_OUT_LOCAL_CDI_ID = "l";
	private static String OPTION_UPDATE = "update";
	private List<String> mandatory_options = new ArrayList<>();
	private ResourceBundle aboutBundle;



	private static int OK_EXIT_CODE = 0;
	private static int BAD_OPTIONS_EXIT_CODE = 1;
	private static int INPUT_ERROR_EXIT_CODE = 2;
	private static int PROCESS_ERROR_EXIT_CODE = 3;

	protected static String CDI_SEPARATOR=",";
	private static String T_OPTION_KEEP = "keep";
	private static String T_OPTION_SPLIT = "split";

	/**
	 * Controller for batch mode, main constructor
	 * @param args
	 * @throws OctopusException
	 * @throws VocabularyException
	 * @throws SQLException 
	 */
	public BatchController(String[] args) throws OctopusException, VocabularyException {
		
		this(args, false);
	}

	/**
	 *  Controller for batch mode, constructor for junit tests
	 * @param args
	 * @param isJunitTest
	 * @throws OctopusException
	 * @throws SQLException 
	 */
	public BatchController(String[] args, boolean isJunitTest) throws OctopusException   {
		super();
		HashMap<String, Object> jsonRes = new HashMap<>();

		// The BatchController can be called directly instead of by the main class. We must have a ExternalResourcesManager instance.
		if (!ExternalResourcesManager.isInsantiated()) {
			
			LOGGER.info(messages.getString("batchcontroller.loadExternalResources"));
			
			try {
				FileInputStream extResourcesConf = new FileInputStream(new File("resources/externalResourcesConfiguration.yaml"));
				ExternalResourcesManager.getInstance(extResourcesConf, "resources/externalResources");
			} catch (Exception e) {
				LOGGER.error(e.getMessage());
			}
		}
		
		//***************************************************************
		this.isJunitTest = isJunitTest;
		aboutBundle = ResourceBundle.getBundle("bundles/about", PreferencesManager.getInstance().getLocale());

		OctopusVersion.check();
		// use altran edmo code for junit tests (preferences.xml file value must be empty)
		if (this.isJunitTest){
			PreferencesManager.getInstance().setEdmoCode(3367);
		}
		//***************************************************************
		initOptionsParser();

		try {
			parseAndFill(args);
		} catch (OctopusException e1) {
			jsonRes.put(GLOBAL_BATCH_SUCCESS, false);
			jsonRes.put(GLOBAL_BATCH_ARGS, String.join(" ", args));
			jsonRes.put(GLOBAL_BATCH_ERROR, e1);
			jsonLogger.info(jsonRes);
			logAndExit(BAD_OPTIONS_EXIT_CODE, e1);
		} catch (IOException e1) {
			jsonRes.put(GLOBAL_BATCH_SUCCESS, false);
			jsonRes.put(GLOBAL_BATCH_ARGS, String.join(" ", args));
			jsonRes.put(GLOBAL_BATCH_ERROR, e1);
			jsonLogger.info(jsonRes);
			exit(INPUT_ERROR_EXIT_CODE, e1);
		} catch (ParseException e1) {
			jsonRes.put(GLOBAL_BATCH_SUCCESS, false);
			jsonRes.put(GLOBAL_BATCH_ARGS, String.join(" ", args));
			jsonRes.put(GLOBAL_BATCH_ERROR, e1);
			jsonLogger.info(jsonRes);
			logAndExit(BAD_OPTIONS_EXIT_CODE, e1);
		}

		boolean success = true;

		try {
			/**
			 * PROCESS
			 */

			if (checkOnly){
				LOGGER.info(MessageFormat.format(messages.getString("batchcontroller.startCheck"), model.getInputPath()));
				success = checkFormat(jsonLogger);
			}else if(update) {
				update();
			}else{
				LOGGER.info(MessageFormat.format(messages.getString("batchcontroller.startExport"), model.getInputPath()));
				List<String> outputFiles = processConversion(jsonLogger);
			}

		} catch (OctopusException e1) {
			LOGGER.error(e1.getMessage());
			//			jsonLogger.info("success={}, options={}, error={}", success, args, e1);
			jsonRes.put(GLOBAL_BATCH_SUCCESS, false);
			jsonRes.put(GLOBAL_BATCH_ARGS, String.join(" ", args));
			jsonRes.put(GLOBAL_BATCH_ERROR, e1);
			jsonLogger.info(jsonRes);
			exit(PROCESS_ERROR_EXIT_CODE, e1);
		} catch (SQLException e) {
			jsonRes.put(GLOBAL_BATCH_SUCCESS, false);
			jsonRes.put(GLOBAL_BATCH_ARGS, String.join(" ", args));
			jsonRes.put(GLOBAL_BATCH_ERROR, e);
			LOGGER.error(e.getCause());
			LOGGER.error(messages.getString("batchcontroller.guiRunning"));
		} 


		//		jsonLogger.info("success={}, options={}, error={}", success, args, "");
		jsonRes.put(GLOBAL_BATCH_SUCCESS, success);
		jsonRes.put(GLOBAL_BATCH_ARGS, String.join(" ", args));
		jsonRes.put(GLOBAL_BATCH_ERROR, "");
		jsonLogger.info(jsonRes);


		exit(OK_EXIT_CODE, null);

	}




	private void update() {
		LOGGER.info(" *** Update external resources *** ");
		// EDMO
		try {
			LOGGER.info("Update EDMO codes");

			int before = EdmoManager.getInstance().getEdmoList().size();
			EdmoManager.getInstance().updateEdmo();
			int after = EdmoManager.getInstance().getEdmoList().size();

			LOGGER.info(MessageFormat.format(messages.getString("preferences.edmoCodeNumber"), before, after));
		} catch (Exception e) {

			String message = e.getMessage();

			LOGGER.error(message);
		}

		// CSR
		try {
			String res=ExternalResourcesManager.getInstance().getCsrListManager().reload();
			LOGGER.info(res);
		} catch (Exception e1) {
			LOGGER.error(e1.getMessage() );
		}

		// BODC
		try {
			// check current versions
			LOGGER.info("check current vocabulary files" );
			HashMap<String, Integer> current = null;
			HashMap<String, Integer> newVersions = null;
			try {
				current = ExternalResourcesManager.getInstance().getSdnVocabularyManager().checkCurrent();
			}catch (Exception e) {
				LOGGER.error(e.getMessage() );
			}

			// reload
			LOGGER.info("download or update vocabulary files");
			try {
				ExternalResourcesManager.getInstance().getSdnVocabularyManager().reload();
			} catch (Exception e) {
				LOGGER.error(e.getMessage() );
			}


			// get collections online
			try {
				newVersions = ExternalResourcesManager.getInstance().getSdnVocabularyManager().readOnlineVersions();
			}catch (Exception e) {
				LOGGER.error(e.getMessage() );
			}


			// read diff in versions
			try {
				List<String> logMessages = ExternalResourcesManager.getInstance().getSdnVocabularyManager().getDiff(current, newVersions);
				for (String message : logMessages) {
					LOGGER.info(message);
				}
			}catch (Exception e) {
				LOGGER.error(e.getMessage());
			}

			// update mappings
			LOGGER.info("update mapping files");

			List<String>  logMessages=ExternalResourcesManager.getInstance().getSdnVocabularyManager().updateVocabMappings();
			for (String message : logMessages) {
				LOGGER.info(message);
			}
				
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		
	}

	private void parseAndFill(String[] args) throws OctopusException, IOException, ParseException {
		try {

			// parse command arguments
			CommandLine cmd = parser.parse( options, args);

			if (cmd.hasOption(OPTION_CHECKONLY)){
				LOGGER.debug("option CHECK");
				checkOnly=true;
				mandatory_options.add(OPTION_I);
			}else if (cmd.hasOption(OPTION_UPDATE)){
				LOGGER.debug("option UPDATE");
				// no other arg required
				update = true;
				return;
			}else {
				mandatory_options.add(OPTION_I);
				mandatory_options.add(OPTION_O);
				mandatory_options.add(OPTION_F);
			}

			// check if mandatory options are present
			for (String option : mandatory_options){
				if (!cmd.hasOption(option)){
					throw new OctopusException(MessageFormat.format(messages.getString("batchcontroller.missingOption"), option));

				}
			}

			// check mandatory options values
			String inputPath = cmd.getOptionValue(OPTION_I).trim();
			if (inputPath.isEmpty()){
				throw new OctopusException(messages.getString("batchcontroller.inputPathEmpty"));
			}


			// log
			LOGGER.info(messages.getString("batchcontroller.argumentsResumeTitle"));
			LOGGER.info(MessageFormat.format(messages.getString("batchcontroller.argumentsInputPath"), inputPath));

			if (!checkOnly){
				String outputPath = cmd.getOptionValue(OPTION_O).trim();
				if (outputPath.isEmpty()){
					throw new OctopusException(messages.getString("batchcontroller.outputPathEmpty"));
				}
				else if (outputPath.equals(inputPath)){
					throw new OctopusException(messages.getString("batchcontroller.outputPathCanNotBeSameAsInput"));
				}
				Format outputFormat = getFormatFromBatchArg(cmd.getOptionValue(OPTION_F));


				String typeString=cmd.getOptionValue(OPTION_T);
				OUTPUT_TYPE type = OUTPUT_TYPE.MULTI;
				if (typeString!=null){
					type = getType(typeString);
				}

				String cdiList = cmd.getOptionValue(OPTION_CDI);
				List<String> list = getCdiList(cdiList);

				String outputLocalCdiIdString = cmd.getOptionValue(OPTION_OUT_LOCAL_CDI_ID);
				String outputLocalCdiId = null ;
				if (outputLocalCdiIdString!=null){
					outputLocalCdiId = outputLocalCdiIdString.trim();
				}
				LOGGER.info(MessageFormat.format(messages.getString("batchcontroller.argumentsOutputPath"), outputPath));
				LOGGER.info(MessageFormat.format(messages.getString("batchcontroller.argumentsOutputFormat"), outputFormat));
				LOGGER.info(MessageFormat.format(messages.getString("batchcontroller.argumentsOutputType"), type));
				LOGGER.info(MessageFormat.format(messages.getString("batchcontroller.argumentsCdiList"), cdiList));
				LOGGER.info(MessageFormat.format(messages.getString("batchcontroller.argumentsOutCDI"), outputLocalCdiId));
				try{
					init(inputPath);
				} catch (IOException e) {
					LOGGER.error(messages.getString("batchcontroller.inputPathError"));
					throw e;
				}
				model.setOutputFormat(outputFormat);
				model.setOutputPath(outputPath);
				// output type may have been initialized in init method (forced for MGD)
				if (model.getOutputType()==null){
					model.setOutputType(type);
				}
				model.setCdiList(list);
				model.setOuputLocalCdiId(outputLocalCdiId);
			}else{
				try{
					init(inputPath);
				} catch (IOException e) {
					LOGGER.error(messages.getString("batchcontroller.inputPathError"));
					throw e;
				}
			}


			checkInput(new File(inputPath));






		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}



	/**
	 * 
	 * @param cdiList : comma separated string CDI list
	 * @return the list of CDIs contained in the comma separated string
	 * @throws OctopusException 
	 */
	private List<String> getCdiList(String cdiList) throws OctopusException {
		List<String> list = new ArrayList<>();
		try{
			if (cdiList!=null){
				cdiList = cdiList.trim();
				for (String cdi: cdiList.split(CDI_SEPARATOR)){
					cdi = cdi.trim();
					list.add(cdi);
				}
			}
		}catch(Exception e){
			LOGGER.error(e.getMessage());
			throw new OctopusException(messages.getString("batchcontroller.cdiListNotReadable"));
		}
		return list;
	}

	/**
	 * 
	 * @param optionValue: the format String from command line argument
	 * @return Format object 
	 * @throws OctopusException
	 */
	private Format getFormatFromBatchArg(String optionValue) throws OctopusException   {
		if (optionValue.trim().equalsIgnoreCase(Format.MEDATLAS_SDN.getName())){
			return Format.MEDATLAS_SDN;
		}
		if (optionValue.trim().equalsIgnoreCase(Format.ODV_SDN.getName())){
			return Format.ODV_SDN;
		}
		if (optionValue.trim().equalsIgnoreCase(Format.CFPOINT.getName())){
			return Format.CFPOINT;
		}
		throw new OctopusException(messages.getString("batchcontroller.unrecognizedOutputFormat"));
	}


	/**
	 * 
	 * @param optionValue: the Output Type String from command line argument
	 * @return OUTPUT_TYPE enum value
	 * @throws OctopusException
	 */
	private OUTPUT_TYPE getType(String optionValue) throws OctopusException  {
		if (optionValue.trim().equalsIgnoreCase(T_OPTION_SPLIT)){
			return OctopusModel.OUTPUT_TYPE.MONO;
		}
		if (optionValue.trim().equalsIgnoreCase(T_OPTION_KEEP)){
			return OctopusModel.OUTPUT_TYPE.MULTI;
		}
		throw new OctopusException(messages.getString("batchcontroller.unrecognizedOutputType"));
	}


	/**
	 * initialize parser options
	 */
	private void initOptionsParser(){
		// create Options object
		options = new Options();


		options.addOption(OPTION_I, true, messages.getString("batchcontroller.argumentsInputPathHelp"));
		options.addOption(OPTION_CHECKONLY, false, messages.getString("batchcontroller.argumentsCheckOnly"));
		options.addOption(OPTION_O, true, messages.getString("batchcontroller.argumentsOutputPathHelp"));
		options.addOption(OPTION_F, true, messages.getString("batchcontroller.argumentsFormatHelp"));
		options.addOption(OPTION_T, true, messages.getString("batchcontroller.argumentsTypeHelp"));
		options.addOption(OPTION_CDI, true, messages.getString("batchcontroller.argumentsCdiListHelp"));
		options.addOption(OPTION_OUT_LOCAL_CDI_ID, true, messages.getString("batchcontroller.argumentsOutputCdiHelp"));
		options.addOption(OPTION_UPDATE, false, messages.getString("batchcontroller.argumentsUpdate"));

		//		mandatory_options.add(OPTION_T);

		parser = new DefaultParser();

		formatter = new HelpFormatter();
	}


	/**
	 * print command usage and exit (see  {@link #exit(int, Exception) exit} method)
	 * @param code
	 * @param e1
	 * @throws OctopusException
	 */
	private void logAndExit(int code, Exception e1)throws OctopusException  {
		LOGGER.error(e1.getMessage());
		formatter.printHelp( "octopus", options , true);
		exit(code, e1);

	}

	/**
	 * if not in junit test, exit with given code, else throw new OctopusException from given Exception
	 * @param code
	 * @param e1
	 * @throws OctopusException
	 */
	private void exit(int code, Exception e1) throws OctopusException {
		if (!isJunitTest){
			System.exit(code);
		}else{
			if (code!=OK_EXIT_CODE){
				throw new OctopusException(e1);
			}
		}
	}
	/**
	 * check if inputPath exist
	 * @param inputPath
	 * @throws OctopusException
	 */
	private void checkInput(File inputPath) throws OctopusException  {
		if (!inputPath.exists()){
			throw new OctopusException(messages.getString("batchcontroller.inputPathInvalid")); 
		}

	}

	@Override
	protected void logStart() {
		LOGGER.info("==================== OCTOPUS BATCH START  ====================");

	}


	public static void main(String[] args) {
		try {
			new BatchController(args);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
	}
}
