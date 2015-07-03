package fr.ifremer.octopus;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.MainController;
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

        log.info("Starting Hello JavaFX and Maven demonstration application");
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");
        
        // Set the application icon.
        this.primaryStage.getIcons().add(new Image("file:resources/images/Octopus-50.png"));
//        String fxmlFile = "/fr/ifremer/octopus/view/Main.fxml";
//        log.debug("Loading FXML for main view from: {}", fxmlFile);
//        FXMLLoader loader = new FXMLLoader();
//        Parent rootNode = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
//
//        log.debug("Showing JFX scene");
//        Scene scene = new Scene(rootNode, 600, 800);
//        scene.getStylesheets().add("/styles/styles.css");
//
//        primaryStage.setTitle("Hello JavaFX and Maven");
//        primaryStage.setScene(scene);
//        primaryStage.show();
        
        
        initRootLayout();
        showView();
    }
    
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("controller/Root.fxml"));
            rootLayout = (BorderPane) loader.load();
//            String fxmlFile = "/fr/ifremer/octopus/view/Root.fxml";
//            loader.load(getClass().getResourceAsStream(fxmlFile));

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the main app.
            MainController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Try to load last opened person file.
//        File file = getPersonFilePath();
//        if (file != null) {
//            loadPersonDataFromFile(file);
//        }
    }
    
    public void showView(){
            try {
                // Load octopus overview.
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(MainApp.class.getResource("controller/OctopusOverview.fxml"));
                BorderPane octopusOverview = (BorderPane) loader.load();

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
