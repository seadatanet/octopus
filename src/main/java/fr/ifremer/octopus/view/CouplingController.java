package fr.ifremer.octopus.view;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.MainApp;
import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.octopus.controller.couplingTable.CouplingRecordFx;
import fr.ifremer.octopus.controller.couplingTable.CouplingTableManager;
import fr.ifremer.sismer_tools.coupling.CouplingRecord;



public class CouplingController {
	static final Logger LOGGER = LogManager.getLogger(CouplingController.class.getName());
	

	/**
	 * Reference to the main application
	 */
	private MainApp mainApp;
	@FXML
	private TableView<CouplingRecordFx> couplingTable;
	@FXML
	TableColumn<CouplingRecordFx, String> local_cdi_id_column;
	@FXML
	TableColumn<CouplingRecordFx, Integer>  modus_column;
	@FXML
	TableColumn<CouplingRecordFx, String>  format_column;
	@FXML
	TableColumn<CouplingRecordFx, String>  path_column;
	@FXML
	TableColumn<CouplingRecordFx, LocalDateTime>  date_column;
	@FXML
	private void initialize() {

		
		local_cdi_id_column.setCellValueFactory(cellData -> cellData.getValue().getLocal_cdi_id());
		modus_column.setCellValueFactory(cellData -> cellData.getValue().getModus().asObject());
		format_column.setCellValueFactory(cellData -> cellData.getValue().getFormat());
		path_column.setCellValueFactory(cellData -> cellData.getValue().getPath());
		date_column.setCellValueFactory(cellData -> cellData.getValue().getDate());
		
		List<CouplingRecord> records;
		try {
			records = CouplingTableManager.getInstance().list();
			ObservableList<CouplingRecordFx> couplingList = FXCollections.observableArrayList();
			for (CouplingRecord cr : records){
				couplingList.add(new CouplingRecordFx(cr));
			}
			
			couplingTable.setItems(couplingList);
		} catch (ClassNotFoundException e) {
			LOGGER.error(e.getMessage());
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		}
		

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
	private void closeCoupling(){
		mainApp.setCenterOverview();
	}

	
	
	

}
