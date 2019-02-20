package fr.ifremer.octopus;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import software.version.SoftwareState;
import software.version.SoftwareState.STATE;
import fr.ifremer.octopus.controller.BatchController;
import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.octopus.view.AboutController;
import fr.ifremer.octopus.view.CouplingController;
import fr.ifremer.octopus.view.OctopusOverviewController;
import fr.ifremer.octopus.view.PreferencesController;
import fr.ifremer.octopus.view.RootController;

public class MainApp extends Application {

	private static final Logger LOGGER = LogManager.getLogger(MainApp.class);
	private Stage primaryStage;
	private BorderPane rootLayout;
	private OctopusOverviewController controller;
	private AnchorPane octopusOverview;

	private PreferencesManager prefsMgr;
	private AnchorPane octopusPreferences,octopusCoupling;
	/**
	 * <pre>
	 * Application entry point for GUI and batch mode.
	 * Launches the GUI if no argument is given, launches batch mode if at least one argument is given 
	 * </pre>
	 * @param args: arguments for batch mode
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("arguments : "+ args);
		if (args.length>0){
			System.out.println("launch Octopus in batch mode");
			BatchController batch = new BatchController(args);
		}else{
			System.out.println("launch Octopus in GUI mode");

			// add GUI log appender
			LoggerContext context = (LoggerContext) LogManager.getContext(false);
			Configuration contextConfiguration = context.getConfiguration();
			Appender appender = contextConfiguration.getAppender("JavaFXLogger");
			contextConfiguration.getRootLogger().addAppender(appender, Level.INFO, null);
			appender.start();
			launch(args);
		}
	}

	@Override
	public void start(Stage primaryStage) throws IOException, OctopusException  {

		LOGGER.info("Starting Octopus application");
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Octopus");

		// Set the application icon.
		this.primaryStage.getIcons().add(new Image("file:resources/images/Octopus-50.png"));

		// load preferences from XMl file
		prefsMgr = PreferencesManager.getInstance();
		prefsMgr.load();
		LOGGER.info("LANGUAGE: "+prefsMgr.getLocale());

		// init and show GUI
		this.primaryStage.setMaximized(true);
		this.primaryStage.setMinWidth(850);
		this.primaryStage.setMinHeight(650);
		initRootLayout();
		showRootLayout();
		initOverview();
		showOverview();
		
		// check version
		checkVersion(primaryStage);
	}

	private void checkVersion(Stage primaryStage) {
		SoftwareState state = OctopusVersion.check();
		if (state ==null){
			return;
		}
		if(state.getState() != STATE.LAST_VERSION){
			ResourceBundle messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());
			String[] lastDate = state.getLastVersionDay().split("-");
			
			String dialogMessage1=MessageFormat.format(messages.getString("rootController.OctopusVersionCurrent"), state.getVersion()  );
			String dialogMessage2=MessageFormat.format(messages.getString("rootController.OctopusVersionUpdateAvailable"), state.getLastVersion(), lastDate[0], lastDate[1], lastDate[2]);
			
			
			final Stage dialog = new Stage();
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.initOwner(primaryStage);
			dialog.setTitle("Octopus Version");
			VBox dialogVbox = new VBox(20);
			dialogVbox.setPadding(new Insets(50, 50, 50, 50));
			
			dialogVbox.getChildren().add(new Text(dialogMessage1));
			dialogVbox.getChildren().add(new Text(dialogMessage2));
			String linkURL="https://www.seadatanet.org/Software/OCTOPUS";
			Hyperlink link = new Hyperlink(linkURL);
			link.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					getHostServices().showDocument(link.getText());
				}
			});
			   dialogVbox.getChildren().add(link);
			
			Button okButton = new Button("OK");
			okButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					dialog.close();
				}
			});
			dialogVbox.setAlignment(Pos.TOP_CENTER);
			dialogVbox.getChildren().add(okButton);
			Scene dialogScene = new Scene(dialogVbox);
			dialog.setScene(dialogScene);
			dialog.show();
		}
	}


	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * initialize stage
	 * @throws IOException 
	 */
	public void initRootLayout() throws IOException {
		// Load root layout from fxml file.
		FXMLLoader loader = new FXMLLoader();
		URL location = MainApp.class.getResource("view/Root.fxml");
		loader.setResources(ResourceBundle.getBundle("bundles.root", prefsMgr.getLocale()));
		loader.setLocation(location);
		rootLayout = (BorderPane) loader.load();
		
		// FIXME we added the experimental -15 because the buttons are out of the screen (too much to the right) on windows when changing language in maximized window.
		rootLayout.setPrefWidth(this.getPrimaryStage().getWidth()-15);
		rootLayout.setPrefHeight(this.getPrimaryStage().getHeight()-15);
		// Give the controller access to the main app.
		RootController controller = loader.getController();
		controller.setMainApp(this);


		  
	}
	public void showRootLayout(){
		// Show the scene containing the root layout.
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);

//		scene.getStylesheets()
//		.add(getClass().getResource("view/whiteTheme.css")
//				.toExternalForm());
		scene.getStylesheets()
		.add(getClass().getResource("view/"+PreferencesManager.getInstance().getThemeFileName())
				.toExternalForm());
		
		primaryStage.show();
	}


	/**
	 * displays the OctopusOverview view
	 */
	public void initOverview(){
		try {
			// Load octopus overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/OctopusOverview.fxml"));
			loader.setResources(ResourceBundle.getBundle("bundles.overview", prefsMgr.getLocale()));
			octopusOverview = (AnchorPane) loader.load();


			// Give the controller access to the main app.
			controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public void showOverview(){
		// Set octopus overview into the center of root layout.
		rootLayout.setCenter(octopusOverview);
	}

	public OctopusOverviewController getController() {
		return controller;
	}

	public void showAbout() {
		// Load octopus overview.
		FXMLLoader loader = new FXMLLoader();
		
		loader.setLocation(getClass().getResource("view/OctopusAbout.fxml"));
		loader.setResources(ResourceBundle.getBundle("bundles.about", prefsMgr.getLocale()));
		BorderPane octopusAbout;
		try {
			octopusAbout = (BorderPane) loader.load();
			// Set octopus overview into the center of root layout.
			rootLayout.setCenter(octopusAbout);
			// Give the controller access to the main app.
			AboutController aController = loader.getController();
			aController.setMainApp(this);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}



	}


	public void showPreferences() {
		// Load octopus overview.
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("view/OctopusPreferences.fxml"));
		loader.setResources(ResourceBundle.getBundle("bundles.preferences", prefsMgr.getLocale()));

		try {
			octopusPreferences = (AnchorPane) loader.load();
			// Set octopus overview into the center of root layout.
			rootLayout.setCenter(octopusPreferences);
			// Give the controller access to the main app.
			PreferencesController pController = loader.getController();
			pController.setMainApp(this);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}

	public void showCoupling() {
		// Load octopus overview.
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("view/OctopusCoupling.fxml"));
		loader.setResources(ResourceBundle.getBundle("bundles.coupling", prefsMgr.getLocale()));

		try {
			octopusCoupling = (AnchorPane) loader.load();
			// Set octopus overview into the center of root layout.
			rootLayout.setCenter(octopusCoupling);
			// Give the controller access to the main app.
			CouplingController cController = loader.getController();
			cController.setMainApp(this);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

	}
	public void setCenterOverview() {
		// Set octopus overview into the center of root layout.
		rootLayout.setCenter(octopusOverview);
	}
	public void setCenterPreferences() {
		// Set octopus overview into the center of root layout.
		rootLayout.setCenter(octopusPreferences);
	}
	public void setCenter(AnchorPane pane) {
		rootLayout.setCenter(pane);

	}



}
