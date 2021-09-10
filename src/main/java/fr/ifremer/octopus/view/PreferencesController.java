package fr.ifremer.octopus.view;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.utils.EdmoManager;
import fr.ifremer.octopus.utils.NetworkUtils;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.octopus.view.edmo.EdmoController;
import fr.ifremer.octopus.view.edmo.EdmoHandler;
import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;
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

public class PreferencesController {
	static final Logger LOGGER = LogManager.getLogger(PreferencesController.class.getName());
	
	static final String LINESEP = System.getProperty("line.separator");

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



	@FXML
	private void initialize() {
		messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());

		// LANGUAGE
		ResourceBundle bundle = ResourceBundle.getBundle("bundles.preferences", PreferencesManager.getInstance().getLocale());
		languageChoiceBox.getItems().add("fr");
		languageChoiceBox.getItems().add("uk");

		// theme order is important: same order as in fr.ifremer.octopus.utils.PreferencesManager.getThemeFileName(int)
		themeChoiceBox.getItems().add("white");
		themeChoiceBox.getItems().add("octopus");
		//		themeChoiceBox.getItems().add("whitePixel"); // 35086: whitePixel profile only for debug

		int index = 0;
		if (PreferencesManager.getInstance().getLocale() == PreferencesManager.LOCALE_FR) {
			index = 0;
		} else {
			index = 1;
		}
		languageChoiceBox.getSelectionModel().select(index);

		// add listener AFTER first select
		languageChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> updateLanguage(newValue));

		// theme
		themeChoiceBox.getSelectionModel().select(PreferencesManager.getInstance().getTheme());
		// add listener AFTER first select
		themeChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> updateTheme(themeChoiceBox.getSelectionModel().getSelectedIndex()));

		// edmo
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

	public void updateEdmoText() {
		String edmo = PreferencesManager.getInstance().getEdmoCode();
		if (!edmo.isEmpty()) {
			int edmo_code = Integer.valueOf(PreferencesManager.getInstance().getEdmoCode());
			String edmo_name = EdmoHandler.getEdmoName(edmo_code);
			edmoCodeValue.setText(edmo_code + " - " + edmo_name);
		}

	}

	public void updateEdmoText(Integer edmo_code, String edmo_name) {
		edmoCodeValue.setText(edmo_code + " - " + edmo_name);
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

	public void setEdmoLabelValue(String value) {

		edmoCodeValue.setText(value);
	}

	/**
	 * called when user changes language in the languageChoiceBox
	 * 
	 * @param newValue
	 */
	private void updateLanguage(String newValue) {
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

	private void updateTheme(int newValue) {
		PreferencesManager.getInstance().setTheme(newValue);
		LOGGER.debug("theme " + newValue + ":" + PreferencesManager.getInstance().getThemeFileName(newValue));
		mainApp.getPrimaryStage().getScene().getStylesheets().add(getClass().getResource(PreferencesManager.getInstance().getThemeFileName(newValue)).toExternalForm());

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
	private void closePreferences() {
		mainApp.setCenterOverview();
	}

	@FXML
	public void browseIn() {
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
	public void browseOut() {
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
	public void browseCouplingPrefix() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle(messages.getString("preferences.chooseDirectory"));
		File selectedFile = dirChooser.showDialog(mainApp.getPrimaryStage());
		if (selectedFile != null) {
			couplingPrefix.setText(selectedFile.getAbsolutePath());
			PreferencesManager.getInstance().setCouplingPrefix(selectedFile.getAbsolutePath());
			PreferencesManager.getInstance().save();
		}
	}

	private void disablePane(boolean disable) {
		languageBox.setDisable(disable);
		languageChoiceBox.setDisable(disable);
		themeChoiceBox.setDisable(disable);
		directoriesBox.setDisable(disable);
		bodcLog.setDisable(disable);
		updateListsButton.setDisable(disable);
		closeButton.setDisable(disable);
	}

	@FXML
	public void updateLists() {
		try {
			bodcLog.clear();
		} catch (Exception e) {
			// TODO: handle exception
		}

		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {

				mainApp.getPrimaryStage().getScene().setCursor(Cursor.WAIT);
				disablePane(true);

				// Launch webservice requests.
				appendTextToBodcLog(Level.INFO, "Update started.");

				// EDMO
				try {

					appendTextToBodcLog(Level.INFO, "Check EDMO codes");
					
					int before = EdmoManager.getInstance().getEdmoList().size();
					EdmoManager.getInstance().updateEdmo();
					int after = EdmoManager.getInstance().getEdmoList().size();

					appendTextToBodcLog(Level.INFO, MessageFormat.format(messages.getString("preferences.edmoCodeNumber"), before, after));
				} catch (Exception e) {


					appendTextToBodcLog(Level.ERROR, e.getMessage());
				}

				// CSR
				try {
					String res=ExternalResourcesManager.getInstance().getCsrListManager().reload();
					appendTextToBodcLog(Level.INFO, res);
				} catch (Exception e1) {
					LOGGER.error(e1.getMessage() );
					appendTextToBodcLog(Level.ERROR, "ERROR: CSR check failed. Please check your internet connection.");
				}

				// BODC
				try {
					HashMap<String, Integer> current = null;
					HashMap<String, Integer> newVersions = null;
					
					// check current versions
					appendTextToBodcLog(Level.INFO, "Check current vocabulary files");
					try {
						current=ExternalResourcesManager.getInstance().getSdnVocabularyManager().checkCurrent();
					}catch (Exception e) {
						appendTextToBodcLog(Level.ERROR, e.getMessage());
					}
					
					// get collections online
					try {
						newVersions=ExternalResourcesManager.getInstance().getSdnVocabularyManager().readOnlineVersions();
					}catch (Exception e) {
						appendTextToBodcLog(Level.ERROR, e.getMessage());
					}

					// read diff in versions
					try {
						List<String> logMessages = ExternalResourcesManager.getInstance().getSdnVocabularyManager().getDiff(current, newVersions);
						for (String message : logMessages) {
							appendTextToBodcLog(Level.INFO, message);
						}
					}catch (Exception e) {
						appendTextToBodcLog(Level.ERROR, e.getMessage());
					}

					// reload
					appendTextToBodcLog(Level.INFO, "Download or update vocabulary files");
					try {
						ExternalResourcesManager.getInstance().getSdnVocabularyManager().reload();
					} catch (Exception e) {
						appendTextToBodcLog(Level.ERROR, e.getMessage());
					}

					// progress bar
					updateProgress(8, 10);

					// progress bar
					updateProgress(10, 10);

					// update mappings
					appendTextToBodcLog(Level.INFO, "Update mapping files");

					try {
						List<String>  logMessages=ExternalResourcesManager.getInstance().getSdnVocabularyManager().updateVocabMappings();
						for (String message : logMessages) {
							appendTextToBodcLog(Level.INFO, message);
						}
					} finally {
						mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
						disablePane(false);
					}

				} catch (Exception e) {
					LOGGER.error(e.getMessage());
					try {
						appendTextToBodcLog(Level.ERROR, e.getMessage());
					} catch (Exception e1) {
						// TODO: handle exception
					}
				} finally {
					mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
					disablePane(false);
				}

				//				updateProgress(10 , 10);
				
				appendTextToBodcLog(Level.INFO, "Update finished.");
				mainApp.getPrimaryStage().getScene().setCursor(Cursor.DEFAULT);
				disablePane(false);

				return null;
			}
		};

		// Testing network availability
		if (NetworkUtils.isInternetUp()) {

			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle(messages.getString("preferences.listsUpdateTitle"));
			alert.setHeaderText(messages.getString("preferences.listsUpdateHeader"));
			alert.setContentText(messages.getString("preferences.listsUpdateContent"));
			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == ButtonType.OK) {
				progress.progressProperty().bind(task.progressProperty());

				Thread th = new Thread(task);
				th.setDaemon(true);
				th.start();

			} else {
				appendTextToBodcLog(Level.INFO, messages.getString("preferences.listsUpdateCancel"));
			}
		} else {
			String message = messages.getString("network.noInternetConnection");

			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(mainApp.getPrimaryStage());
			alert.setTitle(messages.getString("preferences.listsUpdateTitle"));
			alert.setHeaderText(null);
			alert.setContentText(messages.getString("network.noInternetConnection"));
			alert.getButtonTypes().clear();
			ButtonType boutonOk = new ButtonType("Ok");
			alert.getButtonTypes().add(boutonOk);
			alert.showAndWait();

			LOGGER.error(message);
			appendTextToBodcLog(Level.ERROR, message);
		}

	}

	public void switchCouplingEnabled() {
		PreferencesManager.getInstance().setCouplingIsEnabled(couplingCheck.isSelected());
		PreferencesManager.getInstance().save();
	}

	/**
	 * Append text to the BODC log (JavaFX text area).
	 * @param text The text to append.
	 */
	private void appendTextToBodcLog(Level level, String text) {

		LOGGER.log(level, text);
		javafx.application.Platform.runLater( () -> bodcLog.appendText(text + LINESEP) );
	}
}
