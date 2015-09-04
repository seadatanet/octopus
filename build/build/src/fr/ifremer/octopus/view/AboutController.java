package fr.ifremer.octopus.view;

import javafx.fxml.FXML;
import fr.ifremer.octopus.MainApp;



public class AboutController {
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
    	mainApp.closeAbout();
    }
    
    
}
