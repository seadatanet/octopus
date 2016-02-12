package fr.ifremer.octopus.view;

import javafx.fxml.FXML;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.MainApp;



public class AboutController {
	static final Logger LOGGER = LogManager.getLogger(AboutController.class.getName());

	/**
	 * Reference to the main application
	 */
	private MainApp mainApp;
	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	


	@FXML
	private void closeAbout(){
		mainApp.setCenterOverview();
	}


}
