package fr.ifremer.octopus;

import java.io.IOException;
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

import fr.ifremer.octopus.controller.RootController;
import fr.ifremer.octopus.controller.OctopusOverviewController;

public class MainApp extends Application {

	private static final Logger log = LogManager.getLogger(MainApp.class);

	private Stage primaryStage;
	private BorderPane rootLayout;


	public static void main(String[] args) throws Exception {
		log.info("Application launched with args: " );
		for (String a : args){
			log.info(a);
		}
		launch(args);
	}
	public void start(Stage primaryStage) throws Exception {

		log.info("Starting Octopus application");
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
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/Root.fxml"));
			rootLayout = (BorderPane) loader.load();

			
			loader.setResources(ResourceBundle.getBundle("bundles.root", Locale.FRENCH));
			 
			 
			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);


			// Give the controller access to the main app.
			RootController controller = loader.getController();
			controller.setMainApp(this);

			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void showView(){
		try {
			// Load octopus overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("view/OctopusOverview.fxml"));
			loader.setResources(ResourceBundle.getBundle("bundles.overview", Locale.FRENCH));
			AnchorPane octopusOverview = (AnchorPane) loader.load();

			// Set octopus overview into the center of root layout.
			rootLayout.setCenter(octopusOverview);
			// Give the controller access to the main app.
			OctopusOverviewController controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
