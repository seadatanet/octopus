package fr.ifremer.octopus.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.OctopusGUIController;

public class OctopusModel {

	static final Logger logger = LogManager.getLogger(OctopusModel.class.getName());

	public enum OUTPUT_TYPE {MONO, MULTI};
	
	private File inputPath;
	private Format inputFormat;
	private String outputPath;
	private Format outputFormat;
	private OUTPUT_TYPE outputType;
	private List<String> cdiList;

	/**
	 * 
	 * @param inputPath
	 */
	public OctopusModel(String inputPath)  {
		this.cdiList= new ArrayList<>();
		this.inputPath = new File(inputPath);
	}
	/**
	 * 
	 * @param inputPath
	 * @param outputPath
	 * @param outputFormat
	 * @param outputType
	 */
	public OctopusModel(String inputPath, String outputPath, Format outputFormat, OUTPUT_TYPE outputType)  {
		this.cdiList= new ArrayList<>();
		this.inputPath = new File(inputPath);
		this.setOutputPath(outputPath);
		this.outputFormat = outputFormat;
		this.setOutputType(outputType);
	}
	
	
	public File getInputPath() {
		return inputPath;
	}

	public void setInputPath(File inputPath) {
		this.inputPath = inputPath;
	}

	public Format getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(Format outputFormat) {
		this.outputFormat = outputFormat;
	}

	public List<String> getCdiList() {
		return cdiList;
	}

	public void setCdiList(List<String> cdiList) {
		this.cdiList = cdiList;
	}

	public Format getInputFormat() {
		return inputFormat;
	}

	public void setInputFormat(Format inputFormat) {
		this.inputFormat = inputFormat;
		
	}
	public String getOutputPath() {
		return outputPath;
	}
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
	public OUTPUT_TYPE getOutputType() {
		return outputType;
	}
	public void setOutputType(OUTPUT_TYPE outputType) {
		this.outputType = outputType;
	}

	

}
