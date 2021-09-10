package fr.ifremer.octopus.utils;

import java.io.FileInputStream;

import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.octopus.view.edmo.EdmoEntity;
import fr.ifremer.octopus.view.edmo.EdmoHandler;
import fr.ifremer.seadatanet.edmo.EdmoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class EdmoManager {
	static final Logger LOGGER = LogManager.getLogger(EdmoManager.class.getName());
	
	static final String EDMO_FILE = "resources/externalResources/edmo.xml";

	private static ObservableList<EdmoEntity> edmoList;
	private static EdmoManager instance;


	public static EdmoManager getInstance(){
		if (instance==null){
			instance = new EdmoManager();
		}
		return instance;
	}
	private EdmoManager() {
		initEdmo();
		loadEdmo();
	}
	private static void loadEdmo(){

		edmoList = FXCollections.observableArrayList();
		for (EdmoEntity edmoEntity: EdmoHandler.getEdmoList().values()){
			edmoList.add(edmoEntity);
		}

	}
	private static void initEdmo() {
		try {

			EdmoHandler edmoH = new EdmoHandler();
			SAXParserFactory.newInstance().newSAXParser().parse(new FileInputStream(EDMO_FILE), edmoH);
			edmoH.getEdmoList();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}
	public static void updateEdmo() throws OctopusException {

		try {
			
			
			EdmoService.get().refreshOrganisations();
			
		} catch (Exception exG) {
			
			exG.printStackTrace();
			LOGGER.error(exG.getMessage());
			throw new OctopusException("ERROR: EDMO update failed. Please check your internet connection."); // TODO msg
		}
	}
	public ObservableList<EdmoEntity> getEdmoList() {
		return edmoList;
	}
}
