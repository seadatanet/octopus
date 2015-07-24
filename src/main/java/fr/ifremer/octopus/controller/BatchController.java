package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;
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
import fr.ifremer.octopus.model.Format;
import fr.ifremer.octopus.model.OctopusModel;
import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;

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
	private static String OPTION_CDI = "cdi";
	private List<String> mandatory_options = new ArrayList<>();


	private static int OK_EXIT_CODE = 0;
	private static int BAD_OPTIONS_EXIT_CODE = 1;
	private static int INPUT_ERROR_EXIT_CODE = 2;
	private static int PROCESS_ERROR_EXIT_CODE = 3;


	public BatchController(String[] args) throws OctopusException, VocabularyException  {
		this(args, false);
	}
	
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
			process();
		} catch (OctopusException e1) {
			exit(PROCESS_ERROR_EXIT_CODE, e1);
		} 
			exit(OK_EXIT_CODE, null);

	}




	private void parseAndFill(String[] args) throws OctopusException, IOException, ParseException {
		try {
			CommandLine cmd = parser.parse( options, args);

			// check mandatory options are present
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
			List<String> list =null;
			if (cdiList!=null){
				cdiList = cdiList.trim();
				list = new ArrayList<>();
				for (String cdi: cdiList.split(CDI_SEPARATOR)){
					list.add(cdi.trim());
				}
			}


			// log
			LOGGER.info("octopus batch mode arguments:");
			LOGGER.info("input path: " + inputPath);
			LOGGER.info("output path: " + outputPath);
			LOGGER.info("output format: " +outputFormat );
			LOGGER.info("output type: " + type);
			LOGGER.info("CDI list: " + cdiList);

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
			if (cdiList!=null){
				model.setCdiList(list);
			}


		} catch (ParseException e) {
			LOGGER.error(e.getMessage());
			throw e;
		}
	}


	

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

	private OUTPUT_TYPE getType(String optionValue) throws OctopusException  {
		if (optionValue.trim().equalsIgnoreCase(OctopusModel.OUTPUT_TYPE.MONO.toString())){
			return OctopusModel.OUTPUT_TYPE.MONO;
		}
		if (optionValue.trim().equalsIgnoreCase(OctopusModel.OUTPUT_TYPE.MULTI.toString())){
			return OctopusModel.OUTPUT_TYPE.MULTI;
		}
		throw new OctopusException("unrecognized output type");
	}

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



	private void logAndExit(int code, Exception e1)throws OctopusException  {
		formatter.printHelp( "octopus", options , true);
		exit(code, e1);
		
	}
	private void exit(int code, Exception e1) throws OctopusException {
		if (!isJunitTest){
			System.exit(code);
		}else{
			if (code!=OK_EXIT_CODE){
				throw new OctopusException(e1);
			}
		}
	}

	private void checkInput(File inputPath) throws OctopusException  {
		if (!inputPath.exists()){
			throw new OctopusException("input path is not a valid directory or file path"); // TODO: internat
		}

	}
}
