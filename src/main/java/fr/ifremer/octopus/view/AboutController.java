package fr.ifremer.octopus.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

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

	@FXML
	private WebView roadmapweb;
	
	@FXML
	private void initialize() {
		Locale locale = PreferencesManager.getInstance().getLocale();
		LOGGER.debug("initialize about panel with roadmap: " +getClass().getCanonicalName());
		try{
			String pathString="/roadmap/roadmap_"+locale.toString()+".html";
			InputStream inputRoadmap = getClass().getResourceAsStream(pathString);
			if (inputRoadmap==null){
				 pathString="roadmap/roadmap_"+locale.toString()+".html";
				 inputRoadmap = getClass().getResourceAsStream(pathString);
			}
			
			String content = new BufferedReader(new InputStreamReader(inputRoadmap))
			  .lines().collect(Collectors.joining("\n"));
			roadmapweb.getEngine().loadContent(content);
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			LOGGER.error("error on roadmap read");
		} 
	}
	 
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
