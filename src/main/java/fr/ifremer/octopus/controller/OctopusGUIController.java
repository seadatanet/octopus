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
import fr.ifremer.octopus.view.OctopusOverviewController;

public class OctopusGUIController extends AbstractController{

	static final Logger logger = LogManager.getLogger(OctopusGUIController.class.getName());


	private OctopusOverviewController octopusOverviewController;


	public OctopusGUIController() {
		super();
	}


	public OctopusGUIController(String inputPath) {
		super();
		init(new File(inputPath));
	}



	public void log(String logLine){
		if (octopusOverviewController!=null){
			octopusOverviewController.addLogLine(logLine);
		}
		logger.info(logLine);
	}

	
	




	public void setOctopusOverviewController(
			OctopusOverviewController controller) {
		this.octopusOverviewController = controller;

	}


	public void init(File inputPath) {
		model = new OctopusModel(inputPath.getAbsolutePath());
		try {
			model.setInputFormat(getFirstFileInputFormat(inputPath));
		} catch (IOException e) {
			log("error input format");
			e.printStackTrace();
		}

	}

}
