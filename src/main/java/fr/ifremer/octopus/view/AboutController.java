package fr.ifremer.octopus.view;

import java.util.Locale;

import javafx.application.HostServices;
import javafx.fxml.FXML;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.utils.PreferencesManager;



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
	private void openDocumentation(){
		try {
			
			Locale locale = PreferencesManager.getInstance().getLocale();
			String docPath ;
			if (locale==Locale.FRANCE){
				docPath = "resources/manuel.pdf";
			}else{
				docPath = "resources/manual.pdf";
			}

			HostServices hostServices = mainApp.getHostServices();
			hostServices.showDocument(docPath);


		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	@FXML
	private void closeAbout(){
		mainApp.setCenterOverview();
	}


}
