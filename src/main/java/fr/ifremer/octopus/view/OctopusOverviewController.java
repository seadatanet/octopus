package fr.ifremer.octopus.view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
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
import fr.ifremer.octopus.utils.PreferencesManager;
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
	private Button cancelCdiSelect;
	@FXML
	private CheckBox showCdi;
	@FXML
	private TableColumn<SDNCdiIdObservable, String> cdiColumn;




	@FXML
	private void initialize() {
		LOGGER.debug("initialize");
		// disable all but inputPathTextField
		switchGui(false);

		// set cdiTable columns
		cdiTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		cdiColumn.setCellValueFactory(cellData -> cellData.getValue().cdiProperty());

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
			fileChooser.setInitialDirectory(new File(PreferencesManager.getInstance().getOutputDefaultPath()));
			selectedFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		}
		// output = dir
		else
		{
			DirectoryChooser dirChooser = new DirectoryChooser();
			dirChooser.setTitle("Choose output directory"); // TODO
			dirChooser.setInitialDirectory(new File(PreferencesManager.getInstance().getOutputDefaultPath()));
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
	public void initInput() {
		LOGGER.debug("init input");
		LOGGER.info("-----------------------------------------------------------");

		initGui();
		try {
			checkInput();
			octopusGuiController = new OctopusGUIController(inputPathTextField.getText());
			if (octopusGuiController.getModel() !=null){
				showCdi.setVisible(true);
				cdiListManager = new CdiListManager(octopusGuiController);
				LOGGER.debug("init input ok -> switch true");
				switchGui(true);
			}
		} catch (OctopusException e) {
			// TODO
		}
	}
	public void initGui(){
		// reinit GUI
		cdiTable.getItems().clear();
		cdiTable.setVisible(false);
		showCdi.setSelected(false);
		showCdi.setVisible(false);
		cancelCdiSelect.setVisible(false);
		cdiListManager = null;
		switchGui(false);
	}

	private void switchGui(boolean inputOk){
		LOGGER.debug("switch ok "+ inputOk);
		radioMono.setDisable(!inputOk);
		radioMulti.setDisable(!inputOk);
		outputPathTextField.setDisable(!inputOk);
		chooseOut.setDisable(!inputOk);
	}
	private void checkInput() throws OctopusException {
		LOGGER.debug("check input");
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
	
	@FXML
	public void cdiTableDeselect(){
		cdiTable.getSelectionModel().clearSelection();
	}
	/**
	 * get input file(s) CDIs list and show it in the table
	 * @throws OctopusException
	 */
	public void showCdiList() throws OctopusException{
		cdiTable.setVisible(showCdi.isSelected());
		cancelCdiSelect.setVisible(showCdi.isSelected());
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
			mainApp.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
			checkOutput();

			octopusGuiController.getModel().setOutputPath(outputPathTextField.getText());
			octopusGuiController.getModel().setOutputType(getOutputType());
			octopusGuiController.getModel().getCdiList().clear();
			for (SDNCdiIdObservable cdi :cdiTable.getSelectionModel().getSelectedItems()){
				octopusGuiController.getModel().getCdiList().add("SDN:LOCAL::"+cdi.cdiProperty().getValue());
			}
			

			LOGGER.info("export to "+format.getName()); // TODO
			octopusGuiController.getModel().setOutputFormat(format);
			octopusGuiController.process();
		} catch (OctopusException e) {
			LOGGER.error(e.getMessage());
		}finally{
			mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
		}

	}


}
