package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.octopus.model.OctopusModel;
import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;
import fr.ifremer.sismer_tools.seadatanet.Format;

public class BatchController extends AbstractController{
	private static final Logger LOGGER = LogManager.getLogger(BatchController.class);
	private boolean isJunitTest ;

	private Options options;
	private CommandLineParser parser ;
	private HelpFormatter formatter;

	private static String OPTION_I = "i";
	private static String OPTION_O = "o";
	private static String OPTION_F = "f";
	private static String OPTION_T = "t";
	private static String OPTION_CDI = "c";
	private static String OPTION_OUT_LOCAL_CDI_ID = "l";
	private List<String> mandatory_options = new ArrayList<>();


	private static int OK_EXIT_CODE = 0;
	private static int BAD_OPTIONS_EXIT_CODE = 1;
	private static int INPUT_ERROR_EXIT_CODE = 2;
	private static int PROCESS_ERROR_EXIT_CODE = 3;

	protected static String CDI_SEPARATOR=",";


	/**
	 * Controller for batch mode, main constructor
	 * @param args
	 * @throws OctopusException
	 * @throws VocabularyException
	 * @throws SQLException 
	 */
	public BatchController(String[] args) throws OctopusException, VocabularyException  {
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
		this.isJunitTest = isJunitTest;
		initOptionsParser();
		try {
			parseAndFill(args);
		} catch (OctopusException e1) {
			logAndExit(BAD_OPTIONS_EXIT_CODE, e1);
		} catch (IOException e1) {
			exit(INPUT_ERROR_EXIT_CODE, e1);
		} catch (ParseException e1) {
			logAndExit(BAD_OPTIONS_EXIT_CODE, e1);
		}

		try {
			List<String> outputFiles = process();
			if (outputFiles.size()>0){
				LOGGER.info("process ended without error. "+ outputFiles.size() + " files have been written");// TODO
				LOGGER.info(outputFiles);
			}else{
				LOGGER.warn("process ended without error. "+ outputFiles.size() + " files have been written");// TODO
			}
		} catch (OctopusException e1) {
			LOGGER.error(e1.getMessage());
			exit(PROCESS_ERROR_EXIT_CODE, e1);
		} catch (SQLException e) {
			LOGGER.error(e.getCause());
			LOGGER.error("Octopus GUI may be running. Please stop it before launching Octopus in Batch mode");
		} 
		exit(OK_EXIT_CODE, null);

	}




	private void parseAndFill(String[] args) throws OctopusException, IOException, ParseException {
		try {

			// parse command arguments
			CommandLine cmd = parser.parse( options, args);

			// check if mandatory options are present
			for (String option : mandatory_options){
				if (!cmd.hasOption(option)){
					throw new OctopusException("missing option " + option);
				}
			}

			// check mandatory options values
			String inputPath = cmd.getOptionValue(OPTION_I).trim();
			if (inputPath.isEmpty()){
				throw new OctopusException("input path is empty");

			}
			String outputPath = cmd.getOptionValue(OPTION_O).trim();
			if (outputPath.isEmpty()){
				throw new OctopusException("output path is empty");
			}
			Format outputFormat = getFormatFromBatchArg(cmd.getOptionValue(OPTION_F));
			OUTPUT_TYPE type = getType(cmd.getOptionValue(OPTION_T));

			String cdiList = cmd.getOptionValue(OPTION_CDI);
			List<String> list = getCdiList(cdiList);
			
			
			String outputLocalCdiId = cmd.getOptionValue(OPTION_OUT_LOCAL_CDI_ID);

			// log
			LOGGER.info("octopus batch mode arguments:");
			LOGGER.info("input path: " + inputPath);
			LOGGER.info("output path: " + outputPath);
			LOGGER.info("output format: " +outputFormat );
			LOGGER.info("output type: " + type);
			LOGGER.info("CDI list: " + cdiList);
			LOGGER.info("output local_cdi_id: " + outputLocalCdiId);

			checkInput(new File(inputPath));

			try{
				init(new File(inputPath));
			} catch (IOException e) {
				LOGGER.error("input Path error");
				throw e;
			}

			model.setOutputFormat(outputFormat);
			model.setOutputPath(outputPath);
			model.setOutputType(type);
			model.setCdiList(list);
			model.setOuputLocalCdiId(outputLocalCdiId);


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
			throw new OctopusException("CDI list can not be read");// TODO
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
		throw new OctopusException("unrecognized output format");
	}


	/**
	 * 
	 * @param optionValue: the Output Type String from command line argument
	 * @return OUTPUT_TYPE enum value
	 * @throws OctopusException
	 */
	private OUTPUT_TYPE getType(String optionValue) throws OctopusException  {
		if (optionValue.trim().equalsIgnoreCase(OctopusModel.OUTPUT_TYPE.MONO.toString())){
			return OctopusModel.OUTPUT_TYPE.MONO;
		}
		if (optionValue.trim().equalsIgnoreCase(OctopusModel.OUTPUT_TYPE.MULTI.toString())){
			return OctopusModel.OUTPUT_TYPE.MULTI;
		}
		throw new OctopusException("unrecognized output type");
	}


	/**
	 * initialize parser options
	 */
	private void initOptionsParser(){
		// create Options object
		options = new Options();

		options.addOption(OPTION_I, true, "(mandatory) input path: </home/user/...>");
		options.addOption(OPTION_O, true, "(mandatory) output path: </home/user/...>");
		options.addOption(OPTION_F, true, "(mandatory) output format: <medatlas>, <odv> or <cfpoint>");
		options.addOption(OPTION_T, true, "(mandatory) output type: <mono> or <multi>");
		options.addOption(OPTION_CDI, true, "(optionnal) list of local_cdi_id, eg <FI35AAB, FI35AAC>, all cdi are exported if this argument is ommited");


		mandatory_options.add(OPTION_I);
		mandatory_options.add(OPTION_O);
		mandatory_options.add(OPTION_F);
		mandatory_options.add(OPTION_T);

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
			throw new OctopusException("input path is not a valid directory or file path"); // TODO: internat
		}

	}
}
