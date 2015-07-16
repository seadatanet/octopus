package fr.ifremer.octopus.io.driver.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.model.Format;

public class MedatlasDriverImpl implements Driver {

	
	@Override
	public boolean canOpen(String file) throws IOException {
	
		BufferedReader reader = new BufferedReader(new FileReader(file)) ;
		String firstLine = reader.readLine();
		// TODO BGT specs: define more criteria here?
		// Actual criteria: if first character is "*", the file is a medatlas file
		return firstLine.trim().startsWith("*");
	}

	@Override
	public Format getFormat() {
		return Format.MEDATLAS_SDN;
	}

}
