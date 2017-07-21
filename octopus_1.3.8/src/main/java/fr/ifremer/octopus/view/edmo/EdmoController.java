package fr.ifremer.octopus.view.edmo;

import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.utils.EdmoManager;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.octopus.view.PreferencesController;



public class EdmoController {
	static final Logger LOGGER = LogManager.getLogger(EdmoController.class.getName());
	
	/**
	 * Reference to the main application
	 */
	private MainApp mainApp;
    private PreferencesController preferencesController;
    
    @FXML 
	private Pane edmoContainer;
    
  
    
    @FXML
	private void initialize() {
    	// EDMO
		addEdmoTable();
    }
    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    /**
     * 
     * @param preferencesController
     */
    public void setPreferencesController(PreferencesController preferencesController) {
        this.preferencesController = preferencesController;
    }
   
    
	public void addEdmoTable(){
		ResourceBundle  bundle =
				ResourceBundle.getBundle("bundles.edmo", 
						PreferencesManager.getInstance().getLocale());
		
		
		TableColumn<EdmoEntity, String> codeColumn = new TableColumn<EdmoEntity, String>();
		codeColumn.setText(bundle.getString("edmoTable.codeColumn"));
		codeColumn.setCellValueFactory(new PropertyValueFactory<EdmoEntity, String>("code"));
		
		TableColumn<EdmoEntity, String> nameColumn = new TableColumn<EdmoEntity, String>();
		nameColumn.setText(bundle.getString("edmoTable.nameColumn"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<EdmoEntity, String>("name"));
		
		TableColumn<EdmoEntity, String> orgExistColumn = new TableColumn<EdmoEntity, String>();
		orgExistColumn.setText(bundle.getString("edmoTable.orgExistColumn"));
		orgExistColumn.setCellValueFactory(new PropertyValueFactory<EdmoEntity, String>("orgExist"));
		
		TableColumn<EdmoEntity, String> countryColumn = new TableColumn<EdmoEntity, String>();
		countryColumn.setText(bundle.getString("edmoTable.countryColumn"));
		countryColumn.setCellValueFactory(new PropertyValueFactory<EdmoEntity, String>("country"));
		
		
		final TableView<EdmoEntity> tableView = new TableView<EdmoEntity>();
		tableView.setItems(EdmoManager.getInstance().getEdmoList());
		
		tableView.getColumns().addAll(codeColumn, nameColumn, countryColumn, orgExistColumn );
		
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
		    if (newSelection != null) {
		    PreferencesManager.getInstance().setEdmoCode(newSelection.getCode());
		    PreferencesManager.getInstance().save();
		    preferencesController.setEdmoLabelValue(String.valueOf(newSelection.getCode()+ " - " +newSelection.getName()));
		    }
		});
		
		edmoContainer.getChildren().addAll(tableView);
		tableView.setPrefSize(edmoContainer.getPrefWidth(), edmoContainer.getPrefHeight());
	}
    
    
    @FXML
    private void closeEdmo(){
    	mainApp.setCenterPreferences();
    }
    
}
