package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.io.driver.DriverManager;
import fr.ifremer.octopus.io.driver.impl.DriverManagerImpl;
import fr.ifremer.octopus.io.driver.impl.MedatlasDriverImpl;
import fr.ifremer.octopus.model.Format;
import fr.ifremer.octopus.model.InputFileVisitor;
import fr.ifremer.octopus.model.OctopusModel;

public abstract class AbstractController {

	private static final Logger logger = LogManager.getLogger(AbstractController.class);
	private DriverManager driverManager = new DriverManagerImpl();
	protected OctopusModel model;



	public AbstractController() {
		this.driverManager.registerNewDriver(new MedatlasDriverImpl());
	}
	
	
	public void init(File inputPath) throws IOException {
			Format inputFormat = getFirstFileInputFormat(inputPath);
			model = new OctopusModel(inputPath.getAbsolutePath());
			model.setInputFormat(inputFormat);
	}
	
	
	public void process(){
		// is output format different from input?
		if (model.getInputFormat().equals(model.getOutputFormat())){
			logger.info("output format is different from input: need to convert");
		}else{
			logger.info("output and input format are identical: no need to convert");
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
			logger.info("detected input format: "+d.getFormat().getName());
			return d.getFormat();
		}else{
			throw new IOException("unrecognized input format");
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
}
