package fr.ifremer.octopus.view;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.ListBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxBuilder;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.octopus.controller.OctopusGUIController;
import fr.ifremer.octopus.model.OctopusModel.OUTPUT_TYPE;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.octopus.utils.SDNCdiIdObservable;
import fr.ifremer.sismer_tools.seadatanet.Format;

/**
 * Main Octopus View Controller
 * The TableView code inspired from http://code.google.com/p/javafx-demos/
 * @author altran
 *
 */
public class OctopusOverviewController {

	static final Logger LOGGER = LogManager.getLogger(OctopusOverviewController.class.getName());
	ResourceBundle bundle ;

	/**
	 *  Reference to the main application
	 */
	private MainApp mainApp;
	private OctopusGUIController octopusGuiController;




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
	private CheckBox showCdi;

	@FXML 
	private VBox cdiContainer;

	// added from the java code
	private TableView<SDNCdiIdObservable> cdiTable;
	private CheckBox selectAllCheckBox;



	@FXML
	private Button buttonExportMedatlas;
	@FXML
	private Button buttonExportOdv;
	@FXML
	private Button buttonExportCfpoint;
	@FXML
	private Button checkButton;

	// output CDI for input MGD files
	@FXML
	private TextField outCDI;
	@FXML
	private Button chooseOutCdi;
	

	@FXML
	private void initialize() {
		LOGGER.debug("initialize");
		// disable all but inputPathTextField
		switchGui(false);


	}
	public TableView<SDNCdiIdObservable> getCDITable() {
		if (cdiTable == null) {
			// "Selected" column
			TableColumn<SDNCdiIdObservable, Boolean> selectedCol = new TableColumn<SDNCdiIdObservable, Boolean>();
			selectedCol.setMinWidth(50);
			selectedCol.setGraphic(getSelectAllCheckBox());
			selectedCol.setCellValueFactory(new PropertyValueFactory<SDNCdiIdObservable, Boolean>("selected"));
			selectedCol.setCellFactory(new Callback<TableColumn<SDNCdiIdObservable, Boolean>, TableCell<SDNCdiIdObservable, Boolean>>() {
				public TableCell<SDNCdiIdObservable, Boolean> call(TableColumn<SDNCdiIdObservable, Boolean> p) {
					final TableCell<SDNCdiIdObservable, Boolean> cell = new TableCell<SDNCdiIdObservable, Boolean>() {
						@Override
						public void updateItem(final Boolean item, boolean empty) {
							if (item == null)
								return;
							super.updateItem(item, empty);
							if (!isEmpty()) {
								final SDNCdiIdObservable cdi = getTableView().getItems().get(getIndex());
								CheckBox checkBox = new CheckBox();
								checkBox.selectedProperty().bindBidirectional(cdi.selectedProperty());
								setGraphic(checkBox);
							}
							// if one item is deselected, deselect selectAllCheckBox
							if (!item){
								selectAllCheckBox.selectedProperty().setValue(false);
							}
						}
					};
					cell.setAlignment(Pos.CENTER);
					return cell;
				}
			});
			// "CDI" column
			TableColumn<SDNCdiIdObservable, String> cdiCol = new TableColumn<SDNCdiIdObservable, String>();
			cdiCol.setMinWidth(200);
			bundle =
					ResourceBundle.getBundle("bundles.overview", 
							PreferencesManager.getInstance().getLocale());
			cdiCol.setText(bundle.getString("cdiTable.cdiColumn"));
			cdiCol.setCellValueFactory(new PropertyValueFactory<SDNCdiIdObservable, String>("cdi"));


			final TableView<SDNCdiIdObservable> tableView = new TableView<SDNCdiIdObservable>();
			tableView.setItems(getCDIData());
			tableView.getColumns().addAll(selectedCol, cdiCol);

			ListBinding<Boolean> lb = new ListBinding<Boolean>() {
				{
					bind(tableView.getItems());
				}

				@Override
				protected ObservableList<Boolean> computeValue() {
					ObservableList<Boolean> list = FXCollections.observableArrayList();
					for (SDNCdiIdObservable p : tableView.getItems()) {
						list.add(p.getSelected());
					}
					return list;
				}
			};

			lb.addListener(new ChangeListener<ObservableList<Boolean>>() {
				@Override
				public void changed(ObservableValue<? extends ObservableList<Boolean>> arg0, ObservableList<Boolean> arg1,
						ObservableList<Boolean> l) {
					// Checking for an unselected employee in the table view.
					boolean unSelectedFlag = false;
					for (boolean b : l) {
						if (!b) {
							unSelectedFlag = true;
							break;
						}
					}
					/*
					 * If at least one cdi is not selected, then deselecting the check box in the table column header, else if all
					 * employees are selected, then selecting the check box in the header.
					 */
					if (unSelectedFlag) {
						getSelectAllCheckBox().setSelected(false);
					} else {
						getSelectAllCheckBox().setSelected(true);
					}

					// Checking for a selected employee in the table view.
					boolean selectedFlag = false;
					for (boolean b : l) {
						if (!b) {
							selectedFlag = true;
							break;
						}
					}

					/*
					 * If at least one employee is selected, then enabling the "Export" button, else if none of the employees are selected,
					 * then disabling the "Export" button.
					 */
					//							                                      if (selectedFlag) {
					//							                                             enableExportButton();
					//							                                     } else {
					//							                                             disableExportButton();
					//							                                     }
				}
			});

			tableView.getItems().addListener(new InvalidationListener() {

				@Override
				public void invalidated(Observable arg0) {
					LOGGER.debug("invalidated");
				}
			});
			tableView.getItems().addListener(new ListChangeListener<SDNCdiIdObservable>() {

				@Override
				public void onChanged(javafx.collections.ListChangeListener.Change<? extends SDNCdiIdObservable> arg0) {
					LOGGER.debug("changed");
				}
			});
			this.cdiTable = tableView;

		}
		return cdiTable;
	}
	private ObservableList<SDNCdiIdObservable> getCDIData() {
		ObservableList<SDNCdiIdObservable> cdiList = FXCollections.observableArrayList();
		try {
			return octopusGuiController.getCdiListManager().getCdiList();
		} catch (OctopusException e) {
			LOGGER.error(e.getMessage());
			return  cdiList ;
		}

	}
	/**
	 * Lazy getter for the selectAllCheckBox.
	 *
	 * @return selectAllCheckBox
	 */
	@SuppressWarnings("deprecation")
	public CheckBox getSelectAllCheckBox() {
		if (selectAllCheckBox == null) {
			final CheckBox selectAllCheckBox = CheckBoxBuilder.create().build();
			selectAllCheckBox.setSelected(true);
			// Adding EventHandler to the CheckBox to select/deselect all cdis in table.
			selectAllCheckBox.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					// Setting the value in all the cdis.
					for (SDNCdiIdObservable item : getCDITable().getItems()) {
						item.setSelected(selectAllCheckBox.isSelected());
					}
					//                                        getExportButton().setDisable(!selectAllCheckBox.isSelected());
				}
			});

			this.selectAllCheckBox = selectAllCheckBox;
		}
		return selectAllCheckBox;
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
			String def = PreferencesManager.getInstance().getOutputDefaultPath();
			if (def!=null){
				fileChooser.setInitialDirectory(new File(def));
			}
			selectedFile = fileChooser.showSaveDialog(mainApp.getPrimaryStage());
		}
		// output = dir
		else
		{
			DirectoryChooser dirChooser = new DirectoryChooser();
			dirChooser.setTitle("Choose output directory"); // TODO
			String def = PreferencesManager.getInstance().getOutputDefaultPath();
			if (def!=null){
				dirChooser.setInitialDirectory(new File(def));
			}
			selectedFile = dirChooser.showDialog(mainApp.getPrimaryStage());
		}
		if (selectedFile != null) {
			outputPathTextField.setText(selectedFile.getAbsolutePath());
		}
	}
	
	@FXML
	private void openChooseOutCdi(){
		File selectedFile ;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose output file for ODV local CDI Ids"); // TODO
		String def = PreferencesManager.getInstance().getInputDefaultPath();
		if (def!=null){
			fileChooser.setInitialDirectory(new File(def));
		}
		selectedFile = fileChooser.showOpenDialog(mainApp.getPrimaryStage());
		if (selectedFile != null) {
			outCDI.setText(selectedFile.getAbsolutePath());
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
		LOGGER.info("init " + inputPathTextField.getText());

		initGui();

		try {
			checkInput();
			if (octopusGuiController==null){
				octopusGuiController = new OctopusGUIController(inputPathTextField.getText());
			}else{
				octopusGuiController.init(inputPathTextField.getText());
			}
			if (octopusGuiController.getModel() !=null){
				LOGGER.debug("init input ok -> switch true");
				switchGui(true);

			}
		} catch (OctopusException e) {
			LOGGER.error(e.getMessage());
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}
	public void initGui(){
		// reinit GUI
		cdiContainer.getChildren().clear();
		cdiContainer.setVisible(false);
		showCdi.setSelected(false);
		showCdi.setVisible(false);
		cdiTable=null;
		outputPathTextField.setText("");
		outCDI.setText("");
		switchGui(false);
	}

	private void switchGui(boolean inputOk){
		LOGGER.debug("switch ok "+ inputOk);
		
		
		radioMono.setDisable(!inputOk|| (octopusGuiController.getModel().getInputFormat()==Format.MGD_81 
				||  octopusGuiController.getModel().getInputFormat()==Format.MGD_98));
		radioMulti.setDisable(!inputOk|| ( octopusGuiController.getModel().getInputFormat()==Format.MGD_81 
				||  octopusGuiController.getModel().getInputFormat()==Format.MGD_98));
		
		
		outputPathTextField.setDisable(!inputOk);
		chooseOut.setDisable(!inputOk);
		
		checkButton.setDisable(!inputOk);
		
		outCDI.setDisable(!inputOk || ( octopusGuiController.getModel().getInputFormat()==Format.MGD_81 
				&&  octopusGuiController.getModel().getInputFormat()!=Format.MGD_98));
		

				
				if (inputOk){
					File in = new File (octopusGuiController.getModel().getInputPath());
					chooseOutCdi.setVisible(in.isDirectory()&& (octopusGuiController.getModel().getInputFormat()!=Format.MGD_81 
							||  octopusGuiController.getModel().getInputFormat()!=Format.MGD_98));
				}else{
					chooseOutCdi.setVisible(false);
				}
		
		
		
		showCdi.visibleProperty().setValue(
				inputOk 
				&& octopusGuiController.getModel().getInputFormat()!=Format.MGD_81 
				&&  octopusGuiController.getModel().getInputFormat()!=Format.MGD_98);


//		outputCdiContainer.visibleProperty().setValue(
//				inputOk &&
//				(
//						octopusGuiController.getModel().getInputFormat()==Format.MGD_81
//						||octopusGuiController.getModel().getInputFormat()==Format.MGD_98)
//				);


		if (inputOk){
			Format f = octopusGuiController.getModel().getInputFormat();
			boolean disableMedatlas = f!=Format.MEDATLAS_SDN && f!=Format.MEDATLAS_NON_SDN ;
			boolean disableOdv = (f==Format.CFPOINT);
			boolean disableCfPoint = f==Format.MGD_81 || f==Format.MGD_98; 
			buttonExportMedatlas.disableProperty().setValue(disableMedatlas);
			buttonExportOdv.disableProperty().setValue(disableOdv);
			buttonExportCfpoint.disableProperty().setValue(disableCfPoint);
			
			outCDI.setDisable(! (f==Format.MGD_81 || f==Format.MGD_98));
		}else{
			buttonExportMedatlas.disableProperty().setValue(true);
			buttonExportOdv.disableProperty().setValue(true);
			buttonExportCfpoint.disableProperty().setValue(true);
		}
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
		cdiContainer.setVisible(showCdi.isSelected());
		if (showCdi.isSelected()){
			try{
				checkInput();
				cdiContainer.setVisible(showCdi.isSelected());
				if (showCdi.isSelected()){
					try {
						cdiTable=null; // FIXME: cdis are read from input each time we select!!!
						cdiContainer.getChildren().clear();
						cdiContainer.getChildren().addAll(getCDITable());

					} catch (Exception e) {
						LOGGER.error("unable to get CDIs list");// TODO
					}
				}
			}catch(OctopusException e){
				showCdi.setSelected(false);
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
		if (octopusGuiController.getModel().getInputFormat()==Format.MGD_81||octopusGuiController.getModel().getInputFormat()==Format.MGD_98){
			if (outCDI.getText().isEmpty()){
				Alert alert = new Alert(AlertType.ERROR);
				alert.initOwner(mainApp.getPrimaryStage());
				alert.setTitle("Invalid output local CDI ID");// TODO
				alert.setHeaderText("Please set an output local CDI ID");// TODO
				alert.setContentText("output output local CDI ID field is empty"); // TODO
				alert.showAndWait();
				throw new OctopusException("invalid output local CDI ID"); // TODO
			}
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
			if (!outCDI.getText().isEmpty()){
				octopusGuiController.getModel().setOuputLocalCdiId(outCDI.getText());
			}
			for (SDNCdiIdObservable p : getCDITable().getItems()) {
				if (p.getSelected()){
					octopusGuiController.getModel().getCdiList().add(p.cdiProperty().getValue());
				}
			}

			LOGGER.info("export to "+format.getName()); // TODO
			octopusGuiController.getModel().setOutputFormat(format);
			List<String> outputFiles = octopusGuiController.process();
			if (outputFiles.size()>0){
				LOGGER.info("process ended successfully. "+ outputFiles.size() + " files have been written");// TODO
				LOGGER.info(outputFiles);
			}else{
				LOGGER.warn("process ended successfully. "+ outputFiles.size() + " files have been written");// TODO
			}
		} catch (OctopusException e) {
			LOGGER.error(e.getMessage());
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		}finally{
			mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
		}

	}
	@FXML
	public void validate(){
		try {
			octopusGuiController.checkFormat();
		} catch (Exception e) {
			LOGGER.error("file is not valid");// TODO
		}

	}

}
