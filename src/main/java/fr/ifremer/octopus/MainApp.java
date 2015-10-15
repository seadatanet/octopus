package fr.ifremer.octopus;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.BatchController;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.octopus.view.AboutController;
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
	private AnchorPane octopusPreferences;
	/**
	 * <pre>
	 * Application entry point for GUI and batch mode.
	 * Launches the GUI if no argument is given, launches batch mode if at least one argument is given 
	 * </pre>
	 * @param args: arguments for batch mode
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		if (args.length>0){
			BatchController batch = new BatchController(args);
		}else{
			launch(args);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

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
		initRootLayout();
		showRootLayout();
		initOverview();
		showOverview();
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
		try{
			loader.setResources(ResourceBundle.getBundle("bundles.root", prefsMgr.getLocale()));
		}catch(Exception e){
			System.out.println("can not find preferences files. Exit"); // TODO
			LOGGER.error("can not find preferences files. Exit");// TODO
		}
		loader.setLocation(location);
		rootLayout = (BorderPane) loader.load();

		// Give the controller access to the main app.
		RootController controller = loader.getController();
		controller.setMainApp(this);



	}
	public void showRootLayout(){
		// Show the scene containing the root layout.
		Scene scene = new Scene(rootLayout);
		primaryStage.setScene(scene);
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
		AnchorPane octopusAbout;
		try {
			octopusAbout = (AnchorPane) loader.load();
			// Set octopus overview into the center of root layout.
			rootLayout.setCenter(octopusAbout);
			// Give the controller access to the main app.
			AboutController aController = loader.getController();
			aController.setMainApp(this);
		} catch (IOException e) {
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
