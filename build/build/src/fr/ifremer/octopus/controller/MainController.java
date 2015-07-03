package fr.ifremer.octopus.controller;

import fr.ifremer.octopus.MainApp;
import javafx.fxml.FXML;



public class MainController {
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
    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
    
    
}
