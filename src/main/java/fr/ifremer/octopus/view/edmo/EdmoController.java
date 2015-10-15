package fr.ifremer.octopus.view.edmo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.octopus.utils.SDNCdiIdObservable;
import fr.ifremer.octopus.view.PreferencesController;
import fr.ifremer.octopus.webservices.ns_ws_edmo.Edmo_webservice;
import fr.ifremer.octopus.webservices.ns_ws_edmo.Edmo_webserviceLocator;
import fr.ifremer.octopus.webservices.ns_ws_edmo.Edmo_webserviceSoap;



public class EdmoController {
	static final Logger LOGGER = LogManager.getLogger(EdmoController.class.getName());
	static final String EDMO_FILE = "resources/edmo.xml";
	/**
	 * Reference to the main application
	 */
	private MainApp mainApp;
    private PreferencesController preferencesController;
    
    @FXML 
	private Pane edmoContainer;
    
    private ObservableList<EdmoEntity> edmoList;
    
    @FXML
	private void initialize() {
    	// EDMO
    			initEdmo();
    			loadEdmo();
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
    private void loadEdmo(){
		
		edmoList = FXCollections.observableArrayList();
		for (EdmoEntity edmoEntity: EdmoHandler.getEdmoList()){
			edmoList.add(edmoEntity);
		}
		
	}
    public static int initEdmo() {
		int edmoSize = 0;
		try {

			EdmoHandler edmoH = new EdmoHandler();

			SAXParserFactory.newInstance().newSAXParser().parse(new FileInputStream(EDMO_FILE), edmoH);

			List<EdmoEntity> _edmoList = edmoH.getEdmoList();
			edmoSize = _edmoList.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return edmoSize;
	}
    public static void updateEdmo() {
		String edmoURL ="http://seadatanet.maris2.nl/ws/ws_edmo.asmx";

		//		UpdateStatusNemo updateEdmo = (UpdateStatusNemo) ManageLists.getUpdate(Intern.id_edmo);
		int nbEdmo = 0;
		FileOutputStream stream = null;
		String result = "";

		try {
			// Appel au WebService
			Edmo_webservice service = new Edmo_webserviceLocator();
			((Edmo_webserviceLocator) service).setedmo_webserviceSoap12EndpointAddress(edmoURL);
			Edmo_webserviceSoap port = service.getedmo_webserviceSoap12();
			result = port.ws_edmo_get_list();

			if (!result.equals(" ")) {

				// écriture du nouveau fichier
				File file = new java.io.File(EDMO_FILE);
				stream = new FileOutputStream(file.toString());
				OutputStreamWriter osw = new OutputStreamWriter(stream, "UTF-8"); //$NON-NLS-1$
				osw.write(result);
				osw.flush();
				osw.close();

				// stockage des valeurs dans l'objet
				nbEdmo = initEdmo();
			} else {
				LOGGER.error("EDMO update failed");
			}

		} catch (Exception exG) {
			LOGGER.error("EDMO update failed");
		}
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
		tableView.setItems(edmoList);
		
		tableView.getColumns().addAll(codeColumn, nameColumn, countryColumn, orgExistColumn );
		
		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
		    if (newSelection != null) {
		    PreferencesManager.getInstance().setEdmoCode(newSelection.getCode());
		    PreferencesManager.getInstance().save();
		    preferencesController.setEdmoLabelValue(String.valueOf(newSelection.getCode()));
		    }
		});
		
		edmoContainer.getChildren().addAll(tableView);
		tableView.setPrefSize(edmoContainer.getPrefWidth(), edmoContainer.getPrefHeight());
	}
    
    
    @FXML
    private void closeEdmo(){
    	mainApp.setCenterPreferences();
    }
	public static void main(String[] args) {
		updateEdmo();
	}
    
}
