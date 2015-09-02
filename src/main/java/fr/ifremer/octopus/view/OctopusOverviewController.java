package fr.ifremer.octopus.view;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.octopus.controller.OctopusGUIController;
import fr.ifremer.octopus.model.Format;
import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;
import fr.ifremer.octopus.utils.SDNCdiIdObservable;

public class OctopusOverviewController {

	static final Logger LOGGER = LogManager.getLogger(OctopusOverviewController.class.getName());


	/**
	 *  Reference to the main application
	 */
	private MainApp mainApp;
	private OctopusGUIController octopusGuiController;
	private CdiListManager cdiListManager;



	@FXML
	private TextField inputPathTextField;
	@FXML
	private Button chooseInFile;
	@FXML
	private Button chooseInDir;
	@FXML
	private Button 	chooseOut;
	@FXML
	private TextField outputPathTextField;
	@FXML	
	private RadioButton radioMulti;
	@FXML	
	private RadioButton radioMono;
	@FXML
	private TableView<SDNCdiIdObservable> cdiTable;
	@FXML
	private CheckBox showCdi;
	@FXML
	private TableColumn<SDNCdiIdObservable, String> cdiColumn;




	@FXML
	private void initialize() {

		// disable all but inputPathTextField
		switchGui(false);

		// set cdiTable columns
		cdiColumn.setCellValueFactory(cellData -> cellData.getValue().cdiProperty());

		// if inputPathTextField value changed, reinit GUI
		inputPathTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
				if (t1) {
					initGui();
					switchGui(false);
				} 
			}
		});

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
	public void openChooseOut(){
		File selectedFile ;
		File in = new File (octopusGuiController.getModel().getInputPath());

		// input = file and output = multi -> output = file
		if (in.isFile() && getOutputType()==OUTPUT_TYPE.MULTI ){
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Choose output File"); // TODO
			selectedFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		}
		// output = dir
		else
		{
			DirectoryChooser dirChooser = new DirectoryChooser();
			dirChooser.setTitle("Choose output directory"); // TODO
			selectedFile = dirChooser.showDialog(mainApp.getPrimaryStage());
		}
		if (selectedFile != null) {
			outputPathTextField.setText(selectedFile.getAbsolutePath());
		}
	}
	@FXML
	private void inputChanged(KeyEvent event) {

		if (event.getCode()== KeyCode.ENTER || event.getCode()==KeyCode.TAB){
			initInput();
		}
	}

	/**
	 * initialize GUI, load controller if input path is valid
	 */
	private void initInput() {
		LOGGER.info("-----------------------------------------------------------");

		initGui();
		try {
			checkInput();
			octopusGuiController = new OctopusGUIController(inputPathTextField.getText());
			if (octopusGuiController.getModel() !=null){
				showCdi.setVisible(true);
				cdiListManager = new CdiListManager(octopusGuiController);
				switchGui(true);
			}
		} catch (OctopusException e) {
			// TODO
		}
	}
	private void initGui(){
		// reinit GUI
		cdiTable.getItems().clear();
		cdiTable.setVisible(false);
		showCdi.setSelected(false);
		showCdi.setVisible(false);
		cdiListManager = null;


	}

	private void switchGui(boolean inputOk){
		radioMono.setDisable(!inputOk);
		radioMulti.setDisable(!inputOk);
		outputPathTextField.setDisable(!inputOk);
		chooseOut.setDisable(!inputOk);
	}
	private void checkInput() throws OctopusException {

		File input = new File(inputPathTextField.getText());
		if (!input.exists()){
			// Show the error message.
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Invalid path");// TODO
			alert.setHeaderText("Please correct input path field");// TODO
			if (inputPathTextField.getText().isEmpty()){
				alert.setContentText("input path field is empty");// TODO
			}else{
				alert.setContentText("invalid input path");// TODO
			}
			switchGui(false);
			alert.showAndWait();
			throw new OctopusException("invalid input path");// TODO
		}
	}
	@FXML
	public void checkedMono(){
		outputPathTextField.setText("");
	}
	@FXML
	public void checkedMulti(){
		outputPathTextField.setText("");
	}	
	/**
	 * get input file(s) CDIs list and show it in the table
	 * @throws OctopusException
	 */
	public void showCdiList() throws OctopusException{
		cdiTable.setVisible(showCdi.isSelected());
		if (showCdi.isSelected()){
			try {
				cdiTable.setItems(cdiListManager.getCdiList());
			} catch (Exception e) {
				LOGGER.error("unable to get CDIs list");// TODO
			} 
		}
	}

	public void setInputText(String input){
		inputPathTextField.setText(input);
		initInput();
	}


	private void checkOutput() throws OctopusException{

		if (outputPathTextField.getText().isEmpty()){
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle("Invalid path");// TODO
			alert.setHeaderText("Please correct output path field");// TODO
			alert.setContentText("output path field is empty"); // TODO
			alert.showAndWait();
			throw new OctopusException("invalid output path"); // TODO
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
			checkOutput();

			octopusGuiController.getModel().setOutputPath(outputPathTextField.getText());
			octopusGuiController.getModel().setOutputType(getOutputType());

			LOGGER.info("export to "+format.getName()); // TODO
			octopusGuiController.getModel().setOutputFormat(format);
			octopusGuiController.process();
		} catch (OctopusException e) {
			LOGGER.error(e.getMessage());
		}

	}


}
