package fr.ifremer.octopus.io.driver.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.io.driver.DriverManager;


public class DriverManagerImpl implements DriverManager {

	protected List<Driver> drivers;
	
	
	public DriverManagerImpl() {
		this.drivers = new ArrayList<>();
	}
	
	@Override
	public void registerNewDriver(Driver driver) {
		if(driver == null) {
			throw new IllegalArgumentException();
		}
		
		this.drivers.add(driver);
	}

	@Override
	public Driver findDriverForFile(String file) throws IOException {
		
		Driver availableDriver = null;
		
		for(Driver driver : drivers) {
			if(driver.canOpen(file)) {
				availableDriver = driver;
				return availableDriver;
			}
		}
		
		return availableDriver;
	}
}
