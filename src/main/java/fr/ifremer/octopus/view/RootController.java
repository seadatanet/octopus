package fr.ifremer.octopus.view;

import java.awt.Desktop;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.utils.PreferencesManager;



public class RootController {
	static final Logger LOGGER = LogManager.getLogger(RootController.class.getName());
	private ResourceBundle messages;
	/**
	 * Reference to the main application
	 */
	private MainApp mainApp;
	@FXML
	private MenuItem menuOpenFile;
	@FXML
	private MenuItem menuOpenDir;
	@FXML
	private MenuItem menuClose; 
	
	@FXML
	private void initialize() {
		LOGGER.debug("initialize");
		messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());
	}
	
	
	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
	}
	/**
	 * Closes the application.
	 */
	@FXML
	private void handleExit() {
		System.exit(0);
	}

	@FXML
	private void openFile() {
		mainApp.setCenterOverview();
		ResourceBundle messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(messages.getString("rootController.openFile")); 
		String def= PreferencesManager.getInstance().getInputDefaultPath();
		if (def !=null){
			fileChooser.setInitialDirectory(new File(def));
		}
		File selectedFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		if (selectedFile != null) {
			mainApp.getController().setInputText(selectedFile.getAbsolutePath());
		}
	}


	@FXML
	private void openDir() {
		mainApp.setCenterOverview();
		ResourceBundle messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle(messages.getString("rootController.openDirectory")); 
		String def= PreferencesManager.getInstance().getInputDefaultPath();
		if (def !=null){
			dirChooser.setInitialDirectory(new File(def));
		}
		File selectedFile = dirChooser.showDialog(mainApp.getPrimaryStage());
		if (selectedFile != null) {
			mainApp.getController().setInputText(selectedFile.getAbsolutePath());
			mainApp.getController().initInput();
		}
	}
	@FXML
	private void showManual(){
		try {

			Locale locale = PreferencesManager.getInstance().getLocale();
			String docPath ;
			if (locale==PreferencesManager.LOCALE_FR){
				docPath = "resources\\manuel.pdf";
			}else{
				docPath = "resources\\manual.pdf";
			}

			File f = new File(docPath);
			if (f.exists()){
				Desktop.getDesktop().browse(f.toURI());			
			}
			// linux
			else{
				if (locale==Locale.FRANCE){
					docPath = "resources/manuel.pdf";
				}else{
					docPath = "resources/manual.pdf";
				}
				f = new File(docPath);
				if (f.exists()){
					HostServices hostServices = mainApp.getHostServices();
					hostServices.showDocument(docPath);
				}else{
					LOGGER.error(messages.getString("rootController.openManualError"));
				}

			}
			

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	@FXML
	private void showAbout(){

		mainApp.showAbout();
	}
	@FXML
	private void showPreferences(){
		mainApp.showPreferences();
	}
	@FXML
	private void showCoupling(){
		mainApp.showCoupling();
	}
}
