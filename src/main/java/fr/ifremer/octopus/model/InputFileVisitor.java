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

	static final Logger LOGGER = LogManager.getLogger(InputFileVisitor.class.getName());
	String firstFile;
	

	@Override public FileVisitResult visitFile(
			Path aFile, BasicFileAttributes aAttrs
			) throws IOException {
		firstFile = aFile.toAbsolutePath().toString();
		return FileVisitResult.TERMINATE;
	}


	public String getFirstFile() {
		return firstFile;
	}

	@Override  public FileVisitResult preVisitDirectory(
			Path aDir, BasicFileAttributes aAttrs
			) throws IOException {
		return FileVisitResult.CONTINUE;
	}
}
