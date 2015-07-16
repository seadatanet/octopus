package fr.ifremer.octopus.controller;

import java.io.IOException;

import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.io.driver.DriverManager;
import fr.ifremer.octopus.io.driver.impl.DriverManagerImpl;
import fr.ifremer.octopus.io.driver.impl.MedatlasDriverImpl;
import fr.ifremer.octopus.model.Format;

public class Octopus {


	private static Octopus octopus;
	protected DriverManager driverManager = new DriverManagerImpl();


	private Octopus() {
		this.driverManager.registerNewDriver(new MedatlasDriverImpl());
	}


	public static Octopus getInstance(){
		if (octopus==null){
			octopus = new Octopus();
		}
		return octopus;
	}
	public Driver getDriver(String file) throws IOException{
		Driver driver = this.driverManager.findDriverForFile(file);
		return driver;
	}


	public Format getFormat(String file) throws IOException {
		Driver d = getDriver(file);
		return d.getFormat();
		
	}
}
