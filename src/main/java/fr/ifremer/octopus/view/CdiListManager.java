package fr.ifremer.octopus.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.collections.ObservableList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.octopus.controller.OctopusGUIController;
import fr.ifremer.octopus.model.InputFileGetCDIsVisitor;
import fr.ifremer.octopus.utils.SDNCdiIdObservable;

public class CdiListManager {

	static final Logger LOGGER = LogManager.getLogger(CdiListManager.class.getName());

	private OctopusGUIController octopusGuiController;

	public CdiListManager(OctopusGUIController octopusGuiController) {
		this.octopusGuiController = octopusGuiController;
	}


	public ObservableList<SDNCdiIdObservable> getCdiList() throws  OctopusException{

		InputFileGetCDIsVisitor visitor = new InputFileGetCDIsVisitor(octopusGuiController.getModel().getInputFormat());
		try {
			Files.walkFileTree(Paths.get(octopusGuiController.getModel().getInputPath()), visitor);
		} catch (IOException e) {
			throw new OctopusException(e.getMessage());
		}
		ObservableList<SDNCdiIdObservable> cdiList = visitor.getCdiList();


		return cdiList;
	}


}
