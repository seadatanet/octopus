package fr.ifremer.octopus.view;

import java.awt.Desktop;
import java.io.File;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.utils.PreferencesManager;



public class RootController {
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
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open file"); // TODO
		String def= PreferencesManager.getInstance().getInputDefaultPath();
		if (def !=null){
			fileChooser.setInitialDirectory(new File(def));
		}
		File selectedFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		if (selectedFile != null) {
			mainApp.getController().setInputText(selectedFile.getAbsolutePath());
			//    		 mainApp.getController().initInput();
		}
	}


	@FXML
	private void openDir() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Open directory"); // TODO
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
			if (locale==Locale.FRANCE){
				docPath = "resources\\manuel.pdf";
			}else{
				docPath = "resources\\manual.pdf";
			}

//			HostServices hostServices = mainApp.getHostServices();
//			hostServices.showDocument(docPath);
			File f = new File(docPath);
			Desktop.getDesktop().browse(f.toURI());			

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
