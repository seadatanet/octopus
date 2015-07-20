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

import fr.ifremer.octopus.model.Format;
import fr.ifremer.octopus.model.OctopusModel;
import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;

public class BatchController extends AbstractController{
	private static final Logger logger = LogManager.getLogger(BatchController.class);
	private boolean isJunitTest ;
	
	private Options options;
	private CommandLineParser parser ;
	private HelpFormatter formatter;

	private String OPTION_I = "i";
	private String OPTION_O = "o";
	private String OPTION_F = "f";
	private String OPTION_T = "t";
	private String OPTION_CDI = "cdi";
	private List<String> mandatory_options = new ArrayList<>();

	private static int BAD_OPTIONS_EXIT_CODE = 1;
	private static int INPUT_ERROR_EXIT_CODE = 2;


	public BatchController(String[] args, boolean isJunitTest)  {
		super();
		this.isJunitTest = isJunitTest;
		

		initOptionsParser();
		parseAndFill(args);
		
		process();

	}


	private void parseAndFill(String[] args) {
		try {
			CommandLine cmd = parser.parse( options, args);

			// check mandatory options are present
			for (String option : mandatory_options){
				if (!cmd.hasOption(option)){
					logger.error("missing option " + option);
					logAndExit();
				}
			}
			
			// check mandatory options values
			String inputPath = cmd.getOptionValue(OPTION_I).trim();
			if (inputPath.isEmpty()){
				logger.error("input path is empty");
				logAndExit();
				
			}
			String outputPath = cmd.getOptionValue(OPTION_O).trim();
			if (outputPath.isEmpty()){
				logger.error("output path is empty");
				logAndExit();
			}
			Format outputFormat = getFormatFromBatchArg(cmd.getOptionValue(OPTION_F));
			OUTPUT_TYPE type = getType(cmd.getOptionValue(OPTION_T));
			
			// log
			logger.info("octopus batch mode arguments:");
			logger.info("input path: " + inputPath);
			logger.info("output path: " + outputPath);
			logger.info("output format: " +outputFormat );
			logger.info("output type: " + type);
			
			checkInput(new File(inputPath));
			
			try{
				init(new File(inputPath));
			} catch (IOException e) {
				logger.error("input Path error");
				System.exit(INPUT_ERROR_EXIT_CODE);
			}
			
			model.setOutputFormat(outputFormat);
			model.setOutputPath(outputPath);
			model.setOutputType(type);

		} catch (ParseException e) {
			logger.error(e.getMessage());
			logAndExit();
		}
	}


	public BatchController(String[] args)  {
		this(args, false);
	}

	private Format getFormatFromBatchArg(String optionValue)  {
		if (optionValue.trim().equalsIgnoreCase((Format.MEDATLAS_SDN.getName()))){
			return Format.MEDATLAS_SDN;
		}
		if (optionValue.trim().equalsIgnoreCase((Format.ODV_SDN.getName()))){
			return Format.ODV_SDN;
		}
		if (optionValue.trim().equalsIgnoreCase((Format.CFPOINT.getName()))){
			return Format.CFPOINT;
		}
		logger.error("unrecognized output format");
		logAndExit();
		return null;
	}

	private OUTPUT_TYPE getType(String optionValue)  {
		if (optionValue.trim().equalsIgnoreCase((OctopusModel.OUTPUT_TYPE.MONO.toString()))){
			return OctopusModel.OUTPUT_TYPE.MONO;
		}
		if (optionValue.trim().equalsIgnoreCase((OctopusModel.OUTPUT_TYPE.MULTI.toString()))){
			return OctopusModel.OUTPUT_TYPE.MULTI;
		}
		logger.error("unrecognized output type");
		logAndExit();
		return null;
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

	
	private int logAndExit() {
		formatter.printHelp( "octopus", options , true);
		if (!isJunitTest){
			System.exit(BAD_OPTIONS_EXIT_CODE);
			return BAD_OPTIONS_EXIT_CODE;
		}else{
			return BAD_OPTIONS_EXIT_CODE;
		}
	}
	
	private void checkInput(File inputPath)  {
		if (!inputPath.exists()){
			logger.error("input path is not a valid directory or file path"); // TODO internat
			logAndExit();
		}

	}

}
