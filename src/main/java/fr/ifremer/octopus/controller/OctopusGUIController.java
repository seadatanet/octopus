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
import fr.ifremer.octopus.io.driver.impl.MedatlasSDNDriverImpl;
import fr.ifremer.octopus.model.Format;
import fr.ifremer.octopus.model.InputFileVisitor;
import fr.ifremer.octopus.model.OctopusModel;
import fr.ifremer.octopus.view.OctopusOverviewController;

public class OctopusGUIController extends AbstractController{

	static final Logger LOGGER = LogManager.getLogger(OctopusGUIController.class.getName());


	private OctopusOverviewController octopusOverviewController;


	public OctopusGUIController() {
		super();
	}

	public OctopusGUIController(String inputPath)  {
		super();
		try {
			init(new File(inputPath));
		} catch (IOException e) {
			LOGGER.error("init error");
			LOGGER.error(e.getMessage());
		}
	}

	public void setOctopusOverviewController(
			OctopusOverviewController controller) {
		this.octopusOverviewController = controller;

	}

	


	


}
