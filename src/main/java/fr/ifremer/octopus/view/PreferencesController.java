package fr.ifremer.octopus.view;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.octopus.utils.SDNVocabs;
import fr.ifremer.octopus.view.edmo.EdmoController;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;



public class PreferencesController {
	static final Logger LOGGER = LogManager.getLogger(PreferencesController.class.getName());
	

	/**
	 * Reference to the main application
	 */
	private MainApp mainApp;

	@FXML
	private ChoiceBox<String> languageChoiceBox;
	@FXML
	private HBox languageBox;
	@FXML
	private Button edmoChoiceButton;
	@FXML 
	private Label edmoCodeValue;
	@FXML
	private VBox directoriesBox;
	@FXML
	private Button bodcButton;
	@FXML
	private TextField inputDefault;
	@FXML
	private TextField outputDefault;
	@FXML
	private TextArea bodcLog;
	@FXML 
	private ProgressBar progress;
	@FXML
	private Button closeButton;

	@FXML
	private void initialize() {

		// LANGUAGE
		ResourceBundle bundle =
				ResourceBundle.getBundle("bundles.preferences", 
						PreferencesManager.getInstance().getLocale());
		languageChoiceBox.getItems().add("fr");
		languageChoiceBox.getItems().add("uk");


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

		// 	edmo
		edmoCodeValue.setText(PreferencesManager.getInstance().getEdmoCode());

		// DEFAULT DIRECTORIES
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


	@FXML
	public void showEdmo() {
		// Load octopus overview.
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("OctopusEdmoTable.fxml"));
		loader.setResources(ResourceBundle.getBundle("bundles.edmo", PreferencesManager.getInstance().getLocale()));
		AnchorPane octopusEdmoTable;
		try {
			octopusEdmoTable = (AnchorPane) loader.load();
			// Set octopus overview into the center of root layout.
			mainApp.setCenter(octopusEdmoTable);
			// Give the controller access to the main app.
			EdmoController aController = loader.getController();
			aController.setMainApp(this.mainApp);
			aController.setPreferencesController(this);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

	}
	
	public void setEdmoLabelValue(String value){
		edmoCodeValue.setText(value);
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
			LOGGER.error(e.getMessage());
		}
		mainApp.initOverview();
	}

	@FXML
	private void closePreferences(){
		mainApp.setCenterOverview();
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

	private void disablePane(boolean disable){
		languageBox.setDisable(disable);
		directoriesBox.setDisable(disable);
		bodcLog.setDisable(disable);
		bodcButton.setDisable(disable);
		closeButton.setDisable(disable);
	}

	@FXML
	public void updateBODC(){
		bodcLog.clear();

		Task<Void> task = new Task<Void>(){
			@Override
			public Void call(){

				mainApp.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
				disablePane(true);
				String[] vocabsList = {
						SdnVocabularyManager.LIST_P01, SdnVocabularyManager.LIST_P06, SdnVocabularyManager.LIST_P09,
						SdnVocabularyManager.LIST_L22, SdnVocabularyManager.LIST_L33};
				int version;
				HashMap<String, Integer> oldVersions = new HashMap<>();
				HashMap<String, Integer> newVersions = new HashMap<>();
				for (String list: vocabsList){
					try {
						version = SDNVocabs.getInstance().getCf().getCollection(false, list).getDescription().getVersion();
						oldVersions.put(list, version );
					} catch (VocabularyException e) {
						LOGGER.error(e.getMessage());
						bodcLog.appendText(e.getMessage()+System.getProperty("line.separator"));
					}

				}

				// reload
				try{
					SDNVocabs.getInstance().reload();
				}catch(Exception e){
					bodcLog.appendText(e.getMessage()+System.getProperty("line.separator"));
				}
				updateProgress(8 , 10);


				for (String list: vocabsList){
					try {
						version = SDNVocabs.getInstance().getCf().getCollection(false, list).getDescription().getVersion();
						newVersions.put(list, version );
					} catch (VocabularyException e) {
						bodcLog.appendText(e.getMessage()+System.getProperty("line.separator"));
					}

				}
				updateProgress(10 , 10);



				for (String list: vocabsList){

					if (oldVersions.get(list).equals(newVersions.get(list))){
						bodcLog.appendText(list + ": already up to date (version "+  newVersions.get(list) + ")"+System.getProperty("line.separator") );
					}else{
						bodcLog.appendText(list + ": " + oldVersions.get(list) + " -> " + newVersions.get(list)+System.getProperty("line.separator") );
					}
				}
				mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
				disablePane(false);

				return null;                
			}
		};



		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(mainApp.getPrimaryStage());
		alert.setTitle("BODC Vocabularies update");// TODO
		alert.setHeaderText("This will launch BODC vocabularies update");// TODO
		alert.setContentText("This operation can take several minutes"); // TODO
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ButtonType.OK){
			progress.progressProperty().bind(task.progressProperty());

			Thread th = new Thread(task);
			th.setDaemon(true);
			th.start();

		}else{
			bodcLog.appendText("operation cancelled");// TODO
		}

	}
	
	
	

}
