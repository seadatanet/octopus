package fr.ifremer.octopus.io.driver;

import java.io.IOException;

public interface DriverManager {

	void registerNewDriver(Driver driver);
	
	Driver findDriverForFile(String file) throws IOException;
}
