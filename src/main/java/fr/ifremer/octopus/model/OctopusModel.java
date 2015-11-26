package fr.ifremer.octopus.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.sismer_tools.seadatanet.Format;

public class OctopusModel {
	private static final Logger LOGGER = LogManager.getLogger(OctopusModel.class);

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
	private String outputLocalCdiId;
	/**
	 * mapping filename / local_cdi_id for mgd conversion
	 */
	private HashMap<String, String>cdiMap;

	/**
	 * 
	 * @param inputPath: input file or directory path
	 */
	public OctopusModel(String inputPath)  {
		this.cdiList= new ArrayList<String>();
		this.inputPath = inputPath;

		this.inputFile= new File(inputPath);
		this.cdiMap = null;
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
	public void setOuputLocalCdiId(String outputLocalCdiId) throws OctopusException {
		this.outputLocalCdiId = outputLocalCdiId;

		loadOutputCDIs();

	}
	public String getOuputLocalCdiId() {
		return this.outputLocalCdiId ;

	}
	public String getOuputLocalCdiId(String inputFileName) throws OctopusException {
		if (cdiMap==null){
			loadOutputCDIs();
		}
		String cdi=cdiMap.get(inputFileName);

		if (cdi==null){
			throw new OctopusException("no local CDI ID found for file " + inputFileName + " in mapping file " + this.outputLocalCdiId);
		}
		return cdi;

	}
	public void loadOutputCDIs() throws OctopusException{
		if (this.outputLocalCdiId!=null && new File(outputLocalCdiId).exists()){
			String line = "";
			String cvsSplitBy=";";
			BufferedReader br=null;
			try {
				br = new BufferedReader(new FileReader(this.outputLocalCdiId));
				cdiMap=new HashMap<String, String>();
				while ((line = br.readLine()) != null) {

					String[] bean = line.split(cvsSplitBy);
					cdiMap.put(bean[0], bean[1]);

				}
			}  catch (Exception e) {
				LOGGER.error(e.getMessage());
				throw new OctopusException("unable to read mapping file with MGD filenames / local CDI ID");// TODO
			}finally{
				try {
					if (br!=null){
						br.close();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
