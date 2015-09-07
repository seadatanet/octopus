package fr.ifremer.octopus.view;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.utils.PreferencesManager;



public class PreferencesController {
	/**
	 * Reference to the main application
	 */
    private MainApp mainApp;
    
    @FXML
    private ChoiceBox<String> languageChoiceBox;
    @FXML
    private TextField inputDefault;
    @FXML
    private TextField outputDefault;
    
    @FXML
    private void initialize() {
    	 ResourceBundle bundle =
    		      ResourceBundle.getBundle("bundles.preferences", 
    		    		  PreferencesManager.getInstance().getLocale());
//    	languageChoiceBox.getItems().remove(0);
    	languageChoiceBox.getItems().add("fr");
    	languageChoiceBox.getItems().add("en");
    	
    	
    	int index=0;
    	if (PreferencesManager.getInstance().getLocale()== Locale.FRANCE){
			index=0;
		}else{
			index=1;
		}
    	languageChoiceBox.getSelectionModel().select(index);
    	
    	
    	// add listener AFTER first select
    	languageChoiceBox.valueProperty().addListener((observable, oldValue, newValue) ->
    	updateLanguage(newValue));
    	
    	
    	
    	inputDefault.setText(PreferencesManager.getInstance().getInputDefaultPath());
    	outputDefault.setText(PreferencesManager.getInstance().getOutputDefaultPath());
    	
    	
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
     * called when user changes language in the languageChoiceBox
     * @param newValue
     */
    private void updateLanguage(String newValue){
    	PreferencesManager.getInstance().setLocale(newValue);
    	PreferencesManager.getInstance().save();
    	try {
    		mainApp.initRootLayout();
    		mainApp.showRootLayout();
    		mainApp.showPreferences();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    	mainApp.initOverview();
    }
    
    @FXML
    private void closePreferences(){
    	mainApp.closePreferences();
    }
    @FXML
    public void browseIn(){
    	DirectoryChooser dirChooser = new DirectoryChooser();
    	dirChooser.setTitle("Choose directory"); // TODO
    	 File selectedFile = dirChooser.showDialog(mainApp.getPrimaryStage());
    	 if (selectedFile != null) {
    		 inputDefault.setText(selectedFile.getAbsolutePath());
    		 PreferencesManager.getInstance().setInputDefaultPath(selectedFile.getAbsolutePath());
    		 PreferencesManager.getInstance().save();
    	 }
    }
    @FXML
    public void browseOut(){
    	DirectoryChooser dirChooser = new DirectoryChooser();
    	dirChooser.setTitle("Choose directory"); // TODO
    	 File selectedFile = dirChooser.showDialog(mainApp.getPrimaryStage());
    	 if (selectedFile != null) {
    		 outputDefault.setText(selectedFile.getAbsolutePath());
    		 PreferencesManager.getInstance().setOutputDefaultPath(selectedFile.getAbsolutePath());
    		 PreferencesManager.getInstance().save();
    	 }
    }

}
