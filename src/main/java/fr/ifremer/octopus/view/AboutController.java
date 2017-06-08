package fr.ifremer.octopus.view;

import java.io.IOException;
import java.net.URISyntaxException;
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
		LOGGER.debug("initialize about panel with roadmap: " +"/roadmap/roadmap_"+locale.toString()+".txt");
		boolean ok=true;
		try {
			String content = new String(Files.readAllBytes(Paths.get(getClass().getResource("/roadmap/roadmap_"+locale.toString()+".txt").toURI())));
			LOGGER.debug("content :" +content);
			roadmapweb.getEngine().loadContent(content);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.error(e.getMessage());
			ok=false;
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			LOGGER.error(e.getMessage());
			ok=false;
		}
if (!ok){
	try {
		String content = new String(Files.readAllBytes(Paths.get(getClass().getResource("roadmap/roadmap_"+locale.toString()+".txt").toURI())));
		LOGGER.debug("content :" +content);
		roadmapweb.getEngine().loadContent(content);
		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		LOGGER.error(e.getMessage());
		ok=false;
		
	} catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		LOGGER.error(e.getMessage());
		ok=false;
	}
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
