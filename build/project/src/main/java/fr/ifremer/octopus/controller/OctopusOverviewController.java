package fr.ifremer.octopus.controller;

import fr.ifremer.octopus.MainApp;

public class OctopusOverviewController {
	 /**
	  *  Reference to the main application
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
}
