package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.view.OctopusOverviewController;

public class OctopusGUIController extends AbstractController{

	static final Logger LOGGER = LogManager.getLogger(OctopusGUIController.class.getName());


	private OctopusOverviewController octopusOverviewController;




	public OctopusGUIController(String inputPath)  {
		super();
		try {
			init(inputPath);
			
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
