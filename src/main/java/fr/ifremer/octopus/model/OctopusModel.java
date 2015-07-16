package fr.ifremer.octopus.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OctopusModel {

	static final Logger logger = LogManager.getLogger(OctopusModel.class.getName());
	/**
	 * class attributes
	 */

	private File input;
	private Format format;

	
	/**
	 * properties
	 */
	private StringProperty inputPath;


	/**
	 * 
	 * @param inputPath
	 */
	public OctopusModel(String inputPath) {
		this.inputPath = new SimpleStringProperty(inputPath);
		input = new File(inputPath);
		checkInput();
	}

	private void checkInput() {
		if (!input.exists()){
			// already checked in GUI mode
			logger.error("input path is not a valid directory or file path"); // TODO internat
		}else{
			detectInputFormat(input);
		}

	}
	private Format detectInputFormat(File inputFile){
		  InputFileVisitor fileProcessor = new InputFileVisitor();
		    try {
				Files.walkFileTree(Paths.get(inputFile.getAbsolutePath()), fileProcessor);
				format = fileProcessor.getFormat();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return Format.UNKNOWN;
	}
	public StringProperty inputPathProperty(){
		return inputPath;
	}
	public void setInputPath(String inputPath){
		if (new File(inputPath).isDirectory()){
			logger.info("dir");
		}else{
			logger.info("file");
		}

		this.inputPath.set(inputPath);
	}
}
