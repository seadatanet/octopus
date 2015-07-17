package fr.ifremer.octopus.model;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ifremer.octopus.controller.OctopusGUIController;

public class InputFileVisitor extends SimpleFileVisitor<Path> {

	static final Logger logger = LogManager.getLogger(InputFileVisitor.class.getName());
	String firstFile;
	
//	private Format format; 

	@Override public FileVisitResult visitFile(
			Path aFile, BasicFileAttributes aAttrs
			) throws IOException {
//		OctopusGUIController.getInstance().log("Processing file:" + aFile);
//		format =OctopusGUIController.getInstance().getFormat(aFile.toAbsolutePath().toString());
		firstFile = aFile.toAbsolutePath().toString();
		return FileVisitResult.TERMINATE;
	}

//	public Format getFormat() {
//		return format;
//	}

	public String getFirstFile() {
		return firstFile;
	}

	@Override  public FileVisitResult preVisitDirectory(
			Path aDir, BasicFileAttributes aAttrs
			) throws IOException {
//		OctopusGUIController.getInstance().log("Processing directory:" + aDir);
		return FileVisitResult.CONTINUE;
	}
}
