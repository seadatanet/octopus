package fr.ifremer.octopus.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.ifremer.sismer_tools.seadatanet.Format;

public class OctopusModel {


	public enum OUTPUT_TYPE {MONO, MULTI};

	private String inputPath;
	private Format inputFormat;
	private String outputPath;
	private Format outputFormat;
	private OUTPUT_TYPE outputType;
	/**
	 * can be a file or a directory
	 */
	private File inputFile;
	/**
	 * list of local_cdi_id requested
	 */
	private List<String> cdiList;

	/**
	 * 
	 * @param inputPath: input file or directory path
	 */
	public OctopusModel(String inputPath)  {
		this.cdiList= new ArrayList<String>();
		this.inputPath = inputPath;
		
		this.inputFile= new File(inputPath);
	}
	public File getInputFile() {
		return inputFile;
	}
	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
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

	public boolean isMono() {
		return outputType==OUTPUT_TYPE.MONO;
	}


}
