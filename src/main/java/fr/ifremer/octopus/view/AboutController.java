package fr.ifremer.octopus.view;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

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
		boolean ok=true;
//		try{
//			String pathString="/roadmap/roadmap_"+locale.toString()+".txt";
//			URL res = getClass().getResource(pathString);
//			LOGGER.info(pathString + " -> " + res);
//			
//			if (res==null){
//				pathString="roadmap/roadmap_"+locale.toString()+".txt";
//				res = getClass().getResource(pathString);
//				LOGGER.info(pathString + " -> " + res);
//			}
//			
//			String content = new String(Files.readAllBytes(Paths.get(getClass().getResource(pathString).toURI())));
//			
//			LOGGER.debug("content :" +content);
//			roadmapweb.getEngine().loadContent(content);
//			
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			LOGGER.error(e.getMessage());
//			
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			LOGGER.error(e.getMessage());
//		}
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
