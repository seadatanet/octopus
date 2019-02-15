package fr.ifremer.octopus.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.parsers.SAXParserFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.octopus.view.edmo.EdmoEntity;
import fr.ifremer.octopus.view.edmo.EdmoHandler;
import fr.ifremer.octopus.webservices.ns_ws_edmo.Edmo_webservice;
import fr.ifremer.octopus.webservices.ns_ws_edmo.Edmo_webserviceLocator;
import fr.ifremer.octopus.webservices.ns_ws_edmo.Edmo_webserviceSoap;

public class EdmoManager {
	static final Logger LOGGER = LogManager.getLogger(EdmoManager.class.getName());
	static final String EDMO_FILE = "resources/edmo.xml";

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

		FileOutputStream stream = null;
		String result = "";
		int before = edmoList.size();
		int after=-1;

		try {
			// Appel au WebService
			Edmo_webservice service = new Edmo_webserviceLocator();

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
				//				nbEdmo =
				initEdmo();
				loadEdmo();
				after=edmoList.size();
				
				LOGGER.debug("edmo codes number: "+ before + " -> "+ after);
			} else {
				throw new OctopusException("ERROR: EDMO update failed.");// TODO msg
			}

		} catch (Exception exG) {
			throw new OctopusException("ERROR: EDMO update failed. Please check your internet connection."); // TODO msg
		}
	}
	public ObservableList<EdmoEntity> getEdmoList() {
		return edmoList;
	}
}
