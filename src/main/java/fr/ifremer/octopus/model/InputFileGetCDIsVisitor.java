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

import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.medatlas.input.MedatlasInputFileManager;
import fr.ifremer.octopus.utils.SDNCdiIdObservable;
import fr.ifremer.octopus.utils.SDNVocabs;
import fr.ifremer.seadatanet.cfpoint.input.CFReader;
import fr.ifremer.seadatanet.odv.input.OdvReader;
import fr.ifremer.sismer_tools.seadatanet.Format;

public class InputFileGetCDIsVisitor extends SimpleFileVisitor<Path> {

	static final Logger LOGGER = LogManager.getLogger(InputFileGetCDIsVisitor.class.getName());
	private Format format;
	private  ObservableList<SDNCdiIdObservable> cdiList;

	public InputFileGetCDIsVisitor(Format format) {
		this.format = format;
		cdiList = FXCollections.observableArrayList();
	}


	@Override 
	public FileVisitResult visitFile(
			Path aFile, BasicFileAttributes aAttrs
			) throws IOException {

		switch (format) {
		case MEDATLAS_NON_SDN:
		case MEDATLAS_SDN:
			MedatlasInputFileManager mgr;
			try {
				mgr = new MedatlasInputFileManager(aFile.toAbsolutePath().toString(), SDNVocabs.getInstance().getCf());
				for (String cdi: mgr.getInputFileCdiIdList()){
					cdiList.add(new SDNCdiIdObservable(cdi, true));
				}
			} catch (VocabularyException e1) {
				throw new IOException(e1.getMessage());
			} catch (Exception e1) {
				throw new IOException(e1.getMessage());
			}
			break;
		case ODV_SDN:
			try{
				OdvReader reader = new OdvReader(aFile.toAbsolutePath().toString(), SDNVocabs.getInstance().getCf());
				for (String cdi :reader.getInputFileCdiIdList()){
					cdiList.add(new SDNCdiIdObservable(cdi, true));
				}
			}catch (Exception e){
				throw new IOException(e.getMessage());
			}
			break;
		case CFPOINT:
			try{
				CFReader reader = new CFReader(aFile.toAbsolutePath().toString());
				for (String cdi :reader.getInputFileCdiIdList()){
					cdiList.add(new SDNCdiIdObservable(cdi, true));
				}
			}catch (Exception e){
				throw new IOException(e.getMessage());
			}
		default:
			break;
		}
		return FileVisitResult.CONTINUE;
	}



	@Override  
	public FileVisitResult preVisitDirectory(
			Path aDir, BasicFileAttributes aAttrs
			) throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
	public ObservableList<SDNCdiIdObservable> getCdiList(){
		return cdiList;
	}
}
