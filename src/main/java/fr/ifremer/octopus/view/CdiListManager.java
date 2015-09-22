package fr.ifremer.octopus.view;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.AbstractController;
import fr.ifremer.octopus.controller.OctopusException;
import fr.ifremer.octopus.model.InputFileGetCDIsVisitor;
import fr.ifremer.octopus.utils.SDNCdiIdObservable;

public class CdiListManager {

	static final Logger LOGGER = LogManager.getLogger(CdiListManager.class.getName());

	private AbstractController octopusController;

	public CdiListManager(AbstractController abstractController) {
		this.octopusController = abstractController;
	}


	public ObservableList<SDNCdiIdObservable> getCdiList() throws  OctopusException{

		ObservableList<SDNCdiIdObservable> cdiList = FXCollections.observableArrayList();


		if (octopusController.getModel().getInputFormat()!=null){
			InputFileGetCDIsVisitor visitor = new InputFileGetCDIsVisitor(octopusController.getModel().getInputFormat());
			try {
				Files.walkFileTree(Paths.get(octopusController.getModel().getInputPath()), visitor);
			} catch (IOException e) {
				throw new OctopusException(e.getMessage());
			}
			cdiList = visitor.getCdiList();

		}
		return cdiList;

	}


}
