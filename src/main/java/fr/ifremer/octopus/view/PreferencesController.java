package fr.ifremer.octopus.view;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
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
import javafx.scene.control.CheckBox;
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

import sdn.vocabulary.interfaces.ICollection;
import sdn.vocabulary.interfaces.ICollectionMapping;
import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.utils.EdmoManager;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.octopus.utils.SDNVocabs;
import fr.ifremer.octopus.view.edmo.EdmoController;
import fr.ifremer.octopus.view.edmo.EdmoHandler;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;



public class PreferencesController {
	static final Logger LOGGER = LogManager.getLogger(PreferencesController.class.getName());

	private ResourceBundle messages;
	/**
	 * Reference to the main application
	 */
	private MainApp mainApp;

	@FXML
	private ChoiceBox<String> languageChoiceBox;
	@FXML
	private HBox languageBox;
	@FXML
	private ChoiceBox<String> themeChoiceBox;
	@FXML
	private Button edmoChoiceButton;
	@FXML 
	private Label edmoCodeValue;
	@FXML
	private VBox directoriesBox;
	@FXML
	private Button updateListsButton;
	@FXML
	private TextField inputDefault;
	@FXML
	private TextField outputDefault;
	@FXML
	private TextField couplingPrefix;
	@FXML
	private CheckBox couplingCheck;
	@FXML
	private TextArea bodcLog;
	@FXML 
	private ProgressBar progress;
	@FXML
	private Button closeButton;


	private static int nonPresent_bodc=-1;
	@FXML
	private void initialize() {
		messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());

		// LANGUAGE
		ResourceBundle bundle =
				ResourceBundle.getBundle("bundles.preferences", 
						PreferencesManager.getInstance().getLocale());
		languageChoiceBox.getItems().add("fr");
		languageChoiceBox.getItems().add("uk");


		// theme order is important: same order as in fr.ifremer.octopus.utils.PreferencesManager.getThemeFileName(int)
		themeChoiceBox.getItems().add("white");
		themeChoiceBox.getItems().add("octopus");


		int index=0;
		if (PreferencesManager.getInstance().getLocale()== PreferencesManager.LOCALE_FR){
			index=0;
		}else{
			index=1;
		}
		languageChoiceBox.getSelectionModel().select(index);


		// add listener AFTER first select
		languageChoiceBox.valueProperty().addListener((observable, oldValue, newValue) ->
		updateLanguage(newValue));

		//theme
		themeChoiceBox.getSelectionModel().select(PreferencesManager.getInstance().getTheme());
		// add listener AFTER first select
		themeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) ->
		updateTheme(themeChoiceBox.getSelectionModel().getSelectedIndex()));


		// 	edmo
		// call manager to create instance and init EDMO list
		EdmoManager.getInstance();
		updateEdmoText();
		// DEFAULT DIRECTORIES
		inputDefault.setText(PreferencesManager.getInstance().getInputDefaultPath());
		outputDefault.setText(PreferencesManager.getInstance().getOutputDefaultPath());

		// coupling
		couplingCheck.setSelected(PreferencesManager.getInstance().isCouplingEnabled());
		couplingPrefix.setText(PreferencesManager.getInstance().getCouplingPrefix());


	}
	public void updateEdmoText(){
		String edmo=PreferencesManager.getInstance().getEdmoCode();
		if (!edmo.isEmpty()){
			int edmo_code = Integer.valueOf(PreferencesManager.getInstance().getEdmoCode());
			String edmo_name = EdmoHandler.getEdmoName(edmo_code);
			edmoCodeValue.setText(edmo_code + " - "+edmo_name);
		}

	}
	public void updateEdmoText(Integer edmo_code, String edmo_name){
		edmoCodeValue.setText(edmo_code + " - "+edmo_name);
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
	private void updateTheme (int newValue){
		PreferencesManager.getInstance().setTheme(newValue);
		LOGGER.debug("theme "+newValue+":"+ PreferencesManager.getInstance().getThemeFileName(newValue));
		mainApp.getPrimaryStage().getScene().getStylesheets()
		.add(getClass().getResource(PreferencesManager.getInstance().getThemeFileName(newValue))
				.toExternalForm());


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
		dirChooser.setTitle(messages.getString("preferences.chooseDirectory")); 
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
		dirChooser.setTitle(messages.getString("preferences.chooseDirectory")); 
		File selectedFile = dirChooser.showDialog(mainApp.getPrimaryStage());
		if (selectedFile != null) {
			outputDefault.setText(selectedFile.getAbsolutePath());
			PreferencesManager.getInstance().setOutputDefaultPath(selectedFile.getAbsolutePath());
			PreferencesManager.getInstance().save();
		}
	}

	@FXML
	public void browseCouplingPrefix(){
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle(messages.getString("preferences.chooseDirectory")); 
		File selectedFile = dirChooser.showDialog(mainApp.getPrimaryStage());
		if (selectedFile != null) {
			couplingPrefix.setText(selectedFile.getAbsolutePath());
			PreferencesManager.getInstance().setCouplingPrefix(selectedFile.getAbsolutePath());
			PreferencesManager.getInstance().save();
		}
	}
	private void disablePane(boolean disable){
		languageBox.setDisable(disable);
		languageChoiceBox.setDisable(disable);
		themeChoiceBox.setDisable(disable);
		directoriesBox.setDisable(disable);
		bodcLog.setDisable(disable);
		updateListsButton.setDisable(disable);
		closeButton.setDisable(disable);
	}

	@FXML
	public void updateLists(){
		bodcLog.clear();

		Task<Void> task = new Task<Void>(){
			@Override
			public Void call(){

				mainApp.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
				disablePane(true);

				try{
					int before = EdmoManager.getInstance().getEdmoList().size();
					EdmoManager.getInstance().updateEdmo();
					int after = EdmoManager.getInstance().getEdmoList().size();
					bodcLog.appendText(MessageFormat.format(messages.getString("preferences.edmoCodeNumber"), before, after) +System.getProperty("line.separator"));
				}catch(Exception e){

				}
				try{
					String[] vocabsList = {
							SdnVocabularyManager.LIST_P01, SdnVocabularyManager.LIST_P06, SdnVocabularyManager.LIST_P09,
							SdnVocabularyManager.LIST_L22, SdnVocabularyManager.LIST_L33};
					int version;
					HashMap<String, Integer> oldVersions = new HashMap<>();
					HashMap<String, Integer> newVersions = new HashMap<>();
					LOGGER.info("check current vocabulary files");
					bodcLog.appendText("check current vocabulary files"+System.getProperty("line.separator"));
					for (String list: vocabsList){
						try {
							version = SDNVocabs.getInstance().getCf().getCollection(false, list).getDescription().getVersion();
							oldVersions.put(list, version );
						} catch (VocabularyException e) {
							LOGGER.info(e.getMessage());
							bodcLog.appendText(e.getMessage()+System.getProperty("line.separator"));
							// if getCollection offLine raises an exception, that means that the list in not in the local directory -> set old version to 0
							oldVersions.put(list, nonPresent_bodc );
						}

					}

					// reload
					LOGGER.info("download or update vocabulary files");
					bodcLog.appendText("download or update vocabulary files"+System.getProperty("line.separator"));
					try{
						SDNVocabs.getInstance().reload();
					}catch(Exception e){
						bodcLog.appendText(e.getMessage()+System.getProperty("line.separator"));
					}
					updateProgress(8 , 10);

					// get collections online
					for (String list: vocabsList){
						try {
							version = SDNVocabs.getInstance().getCf().getCollection(true, list).getDescription().getVersion();
							newVersions.put(list, version );
						} catch (VocabularyException e) {
							bodcLog.appendText(e.getMessage()+System.getProperty("line.separator"));
						}

					}
					updateProgress(10 , 10);


					try{
						for (String list: vocabsList){

							if (oldVersions.get(list).equals(newVersions.get(list))){
								bodcLog.appendText(MessageFormat.format(messages.getString("preferences.listAlreadyUpToDate"), list, newVersions.get(list)) +System.getProperty("line.separator"));
							}else{
								int old=oldVersions.get(list) ;
								String oldString;
								if (old==nonPresent_bodc){
									oldString = "not present";// TODO
								}else{
									oldString=String.valueOf(old);
								}
								bodcLog.appendText(list + ": " +oldString+ " -> " + newVersions.get(list)+System.getProperty("line.separator") );
							}
						}
					}catch(Exception e){
						LOGGER.error(e.getMessage());
						bodcLog.appendText(e.getMessage()+System.getProperty("line.separator"));
					}

					//mappings
					LOGGER.info("update mapping files");
					bodcLog.appendText("update mapping files"+System.getProperty("line.separator"));
					ICollection p06 = SDNVocabs.getInstance().getCf().getCollection(true, "P06");
					ICollection p09 = SDNVocabs.getInstance().getCf().getCollection(true, "P09");
					ICollection p02 = SDNVocabs.getInstance().getCf().getCollection(true, "P02");
					ICollection p01 = SDNVocabs.getInstance().getCf().getCollection(true, "P01");

					try {
						ICollectionMapping p06_from_P09 = SDNVocabs.getInstance().getCf().getMapping(true, p06, p09.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P09));
						ICollectionMapping p01_from_P09 = SDNVocabs.getInstance().getCf().getMapping(true, p01, p09.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P09));
						ICollectionMapping p01_from_P02 = SDNVocabs.getInstance().getCf().getMapping(true, p01, p02.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P02));
					} catch (VocabularyException e) {
						bodcLog.appendText(e.getMessage()+System.getProperty("line.separator"));
						LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
					}finally{
						mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
						disablePane(false);
					}


				}catch(Exception e){
					LOGGER.error(e.getMessage());
					bodcLog.appendText(e.getMessage()+System.getProperty("line.separator"));
				}finally{
					mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
					disablePane(false);
				}

				mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
				disablePane(false);

				return null;                
			}
		};



		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initOwner(mainApp.getPrimaryStage());
		alert.setTitle(messages.getString("preferences.listsUpdateTitle"));
		alert.setHeaderText(messages.getString("preferences.listsUpdateHeader"));
		alert.setContentText(messages.getString("preferences.listsUpdateContent")); 
		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == ButtonType.OK){
			progress.progressProperty().bind(task.progressProperty());

			Thread th = new Thread(task);
			th.setDaemon(true);
			th.start();

		}else{
			bodcLog.appendText(messages.getString("preferences.listsUpdateCancel"));
		}

	}

	public void switchCouplingEnabled(){
		PreferencesManager.getInstance().setCouplingIsEnabled(couplingCheck.isSelected());
		PreferencesManager.getInstance().save();
	}



}
