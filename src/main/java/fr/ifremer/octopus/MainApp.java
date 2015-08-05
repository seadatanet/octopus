package fr.ifremer.octopus;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
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
import fr.ifremer.octopus.view.AboutController;
import fr.ifremer.octopus.view.OctopusOverviewController;
import fr.ifremer.octopus.view.RootController;

public class MainApp extends Application {

	private static final Logger LOGGER = LogManager.getLogger(MainApp.class);

	private Stage primaryStage;
	private BorderPane rootLayout;
	private OctopusOverviewController controller;
	private AnchorPane octopusOverview;
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

		initRootLayout();
		showView();
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
			loader.setResources(ResourceBundle.getBundle("bundles.root", Locale.FRENCH));
			loader.setLocation(location);
			rootLayout = (BorderPane) loader.load();


			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);


			// Give the controller access to the main app.
			RootController controller = loader.getController();
			controller.setMainApp(this);

			primaryStage.show();

	}
	/**
	 * displays the OctopusOverview view
	 */
	public void showView(){
		try {
			// Load octopus overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/OctopusOverview.fxml"));
			loader.setResources(ResourceBundle.getBundle("bundles.overview", Locale.FRENCH));
			octopusOverview = (AnchorPane) loader.load();

			// Set octopus overview into the center of root layout.
			rootLayout.setCenter(octopusOverview);
			// Give the controller access to the main app.
			controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public OctopusOverviewController getController() {
		return controller;
	}

	public void showAbout() {
					// Load octopus overview.
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource("view/OctopusAbout.fxml"));
					loader.setResources(ResourceBundle.getBundle("bundles.about", Locale.FRENCH));
					AnchorPane octopusAbout;
					try {
						octopusAbout = (AnchorPane) loader.load();
						// Set octopus overview into the center of root layout.
						rootLayout.setCenter(octopusAbout);
						// Give the controller access to the main app.
						AboutController aController = loader.getController();
						aController.setMainApp(this);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					
		
	}

	public void closeAbout() {
		// Set octopus overview into the center of root layout.
		rootLayout.setCenter(octopusOverview);
		
	}
}
