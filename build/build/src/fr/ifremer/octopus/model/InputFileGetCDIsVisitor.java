package fr.ifremer.octopus.model;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.OctopusGUIController;
import fr.ifremer.octopus.utils.SDNCdiIdObservable;
import fr.ifremer.octopus.utils.SDNVocabs;
import fr.ifremer.seadatanet.splitter.CFSplitter;
import fr.ifremer.seadatanet.splitter.SdnSplitter;
import fr.ifremer.seadatanet.splitter.bean.SdnCDIId;

public class InputFileGetCDIsVisitor extends SimpleFileVisitor<Path> {

	static final Logger LOGGER = LogManager.getLogger(InputFileGetCDIsVisitor.class.getName());
	private Format format;
	private  ObservableList<SDNCdiIdObservable> cdiList;

	public InputFileGetCDIsVisitor(Format format) {
		this.format = format;
		cdiList = FXCollections.observableArrayList();
	}


	@Override public FileVisitResult visitFile(
			Path aFile, BasicFileAttributes aAttrs
			) throws IOException {

		switch (format) {
		case MEDATLAS_SDN:
		case ODV_SDN:
			try{
				SdnSplitter splitterSDN = new SdnSplitter(aFile.toAbsolutePath().toString(), "/tmp", "odv", 
						null, 1L, SDNVocabs.getInstance().getCf());
				for (SdnCDIId cdi :splitterSDN.getInputFileCdiIdList()){
					cdiList.add(new SDNCdiIdObservable(cdi));
				}
			}catch (Exception e){
				throw new IOException(e.getMessage());
			}
			break;
		case CFPOINT:
			try{
				CFSplitter splitterCF = new CFSplitter(aFile.toAbsolutePath().toString(), "/tmp", "cfpoint", 
						null, 1L, SDNVocabs.getInstance().getCf());
				for (SdnCDIId cdi :splitterCF.getInputFileCdiIdList()){
					cdiList.add(new SDNCdiIdObservable(cdi));
				}
			}catch (Exception e){
				throw new IOException(e.getMessage());
			}
		default:
			break;
		}
		return FileVisitResult.CONTINUE;
	}



	@Override  public FileVisitResult preVisitDirectory(
			Path aDir, BasicFileAttributes aAttrs
			) throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
	public ObservableList<SDNCdiIdObservable> getCdiList(){
		return cdiList;
	}
}
