package fr.ifremer.octopus.view;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.controller.OctopusGUIController;
import fr.ifremer.octopus.model.OctopusModel;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class OctopusOverviewController {

	static final Logger logger = LogManager.getLogger(OctopusOverviewController.class.getName());


	/**
	 *  Reference to the main application
	 */
	private MainApp mainApp;
	private OctopusGUIController octopusGuiController;

	

	@FXML
	private TextField inputPathTextField;
	// to keep the current value, to be able to detect a change
	private String inputPathTmp="";


	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	@FXML
	private void inputChanged(KeyEvent event){
		if ( (event.getCode()== KeyCode.ENTER || event.getCode()==KeyCode.TAB)&& 
				! inputPathTmp.equals(inputPathTextField.getText())	)
				{
			inputPathTmp = inputPathTextField.getText();
			File input = new File(inputPathTextField.getText());

			if (!input.exists()){
				// Show the error message.
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setTitle("Invalid path");
				alert.setHeaderText("Please correct input path field");
				alert.setContentText(inputPathTextField.getText() + " does not exist");
				alert.showAndWait();
			}else{
				octopusGuiController = new OctopusGUIController(inputPathTextField.getText());
			}
		}
	}

	public void setController(OctopusGUIController controller) {
		this.octopusGuiController = controller;
	}

}
