package fr.ifremer.octopus.view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.octopus.controller.OctopusGUIController;
import fr.ifremer.octopus.model.Format;
import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;

public class OctopusOverviewController {

	static final Logger LOGGER = LogManager.getLogger(OctopusOverviewController.class.getName());


	/**
	 *  Reference to the main application
	 */
	private MainApp mainApp;
	private OctopusGUIController octopusGuiController;



	@FXML
	private TextField inputPathTextField;
	@FXML
	private TextField outputPathTextField;
	@FXML	
	private RadioButton radioMulti;
	@FXML	
	private RadioButton radioMono;
	
public void setInputText(String input){
	inputPathTextField.setText(input);
}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	private void checkInput() throws OctopusException{
		File input = new File(inputPathTextField.getText());

		if (!input.exists()){
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Invalid path");
			alert.setHeaderText("Please correct input path field");
			if (inputPathTextField.getText().isEmpty()){
				alert.setContentText("input path field is empty");
			}else{
				alert.setContentText("invalid input path");
			}
			alert.showAndWait();
			throw new OctopusException("invalid input path");
		}else{
			// init a new controller, which creates a new model
			
		}
	}
	private void checkOutput() throws OctopusException{
		
		if (outputPathTextField.getText().isEmpty()){
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Invalid path");
			alert.setHeaderText("Please correct output path field");
			alert.setContentText("output path field is empty");
			alert.showAndWait();
			throw new OctopusException("invalid output path");
		}
		
			
	}
	private OUTPUT_TYPE getOutputType(){
		if (radioMono.isSelected()){
			return (OUTPUT_TYPE.MONO);
		}else{
			return(OUTPUT_TYPE.MULTI);
		}
	}
	
	
	@FXML
	public void exportToMed(){
			export(Format.MEDATLAS_SDN);
	}
	@FXML
	public void exportToODV(){
			export(Format.ODV_SDN);
	}
	@FXML
	public void exportToCfPoint(){
			export(Format.CFPOINT);
	}
	
	
	private void export(Format format){
		
		try {
			checkInput();
			checkOutput();
			
			octopusGuiController = new OctopusGUIController(inputPathTextField.getText());
			octopusGuiController.getModel().setOutputPath(outputPathTextField.getText());
			octopusGuiController.getModel().setOutputType(getOutputType());
			
			LOGGER.info("export to "+format.getName());
			octopusGuiController.getModel().setOutputFormat(format);
			octopusGuiController.process();
		} catch (OctopusException e) {
			LOGGER.error(e.getMessage());
		}
		
	}
	
	public void setController(OctopusGUIController controller) {
		this.octopusGuiController = controller;
	}

}
