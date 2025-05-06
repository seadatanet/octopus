package fr.ifremer.octopus.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.medatlas.resources.Version;
import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.OctopusVersion;
import fr.ifremer.octopus.utils.PreferencesManager;



public class AboutController {
	static final Logger LOGGER = LogManager.getLogger(AboutController.class.getName());
	/**
	 * Reference to the main application
	 */
	private MainApp mainApp;

	@FXML
	private WebView changeLogWebView;
	@FXML
	private WebView presentationWebView;
	@FXML
	private Label aboutVersion;
	
	@FXML
	private void initialize() {
		Locale locale = PreferencesManager.getInstance().getLocale();
		LOGGER.debug("initialize about panel : " +getClass().getCanonicalName());
		try{
			String pathString="/changelog/changelog_"+locale.toString()+".html";
			InputStream inputRoadmap = getClass().getResourceAsStream(pathString);
			if (inputRoadmap==null){
				 pathString="changelog/changelog_"+locale.toString()+".html";
				 inputRoadmap = getClass().getResourceAsStream(pathString);
			}
			
			String content = new BufferedReader(new InputStreamReader(inputRoadmap))
			  .lines().collect(Collectors.joining("\n"));
			changeLogWebView.getEngine().loadContent(content);
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			LOGGER.error("error on changeLog read");
		} 
		
		try{
			String pathString="/presentation/presentation_"+locale.toString()+".html";
			InputStream inputRoadmap = getClass().getResourceAsStream(pathString);
			if (inputRoadmap==null){
				 pathString="presentation/presentation_"+locale.toString()+".html";
				 inputRoadmap = getClass().getResourceAsStream(pathString);
			}
			
			String content = new BufferedReader(new InputStreamReader(inputRoadmap)).lines()
					.collect(Collectors.joining("\n"));

			// Charger l'image et la convertir en base64
			InputStream imageStream = getClass().getResourceAsStream("/images/lgplv3-147x51.png");
			if (imageStream != null) {
				byte[] imageBytes = IOUtils.toByteArray(imageStream);
				String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);

				content = content.replace("<img id=\"lgplLogo\" alt=\"LGPL v3 Logo\">",
						"<img id=\"lgplLogo\" alt=\"LGPL v3 Logo\" src=\"data:image/png;base64," + base64Image + "\">");
			}

			presentationWebView.getEngine().loadContent(content);			
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			LOGGER.error("error on presentation read");
		}
		
		aboutVersion.setText(OctopusVersion.getVersion());
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
