package fr.ifremer.octopus.controller;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import fr.ifremer.octopus.io.driver.Driver;
import fr.ifremer.octopus.io.driver.DriverManager;
import fr.ifremer.octopus.io.driver.impl.CFPointDriverImpl;
import fr.ifremer.octopus.io.driver.impl.DriverManagerImpl;
import fr.ifremer.octopus.io.driver.impl.MedatlasSDNDriverImpl;
import fr.ifremer.octopus.io.driver.impl.OdvSDNDriverImpl;
import fr.ifremer.octopus.utils.PreferencesManager;
import fr.ifremer.sismer_tools.seadatanet.Format;

public class DriverTest {
	private static final Logger LOGGER = LogManager.getLogger(DriverTest.class);
	private static String PWD = null;
	private static DriverManager driverManager = null;
	
	@BeforeClass
	public static void before(){
		PWD = new File("##").getAbsolutePath().replace("#", "") + "src/test/resources/";
		driverManager = new DriverManagerImpl();
		driverManager.registerNewDriver(new MedatlasSDNDriverImpl());
		driverManager.registerNewDriver(new OdvSDNDriverImpl());
		driverManager.registerNewDriver(new CFPointDriverImpl());
	}
	
	@Test
	public void medSDNTest() {
		
		try {
			Driver d = driverManager.findDriverForFile(PWD+"medatlas/input/profile/diap");
			LOGGER.info(d.getFormat());
			Assert.assertTrue(d.getFormat().equals(Format.MEDATLAS_SDN));
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}
	@Test
	public void medNonSDNTest() {
		
		try {
			Driver d = driverManager.findDriverForFile(PWD+"medatlas/inputNonSDN/profile/medatlasNonSdn.med");
			LOGGER.info(d.getFormat());
			Assert.assertTrue(d.getFormat().equals(Format.MEDATLAS_NON_SDN));
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}
	@Test
	public void odvTtest() {
		
		try {
			Driver d = driverManager.findDriverForFile(PWD+"odv/input/profile/diap.txt");
			LOGGER.info(d.getFormat());
			Assert.assertTrue(d.getFormat().equals(Format.ODV_SDN));
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}
	@Test
	public void cfPointTest() {
		
		try {
			Driver d = driverManager.findDriverForFile(PWD+"cfpoint/input/profile/diap.nc");
			LOGGER.info(d.getFormat());
			Assert.assertTrue(d.getFormat().equals(Format.CFPOINT));
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}
	@Test
	public void debugEncodingODVWithBOMTest() {
		
		try {
			try {
				PreferencesManager.getInstance().load();
			} catch (OctopusException e) {
				e.printStackTrace();
			}
			Driver d = driverManager.findDriverForFile(PWD+"odv/input/profile/00563439_ODV_withBOM.txt");
//			Driver d = driverManager.findDriverForFile(PWD+"tmp/dick/00563439_ODV.txt");
			LOGGER.info("format is :" +d.getFormat());
			Assert.assertTrue(d.getFormat().equals(Format.ODV_SDN));
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			e.printStackTrace();
		}
	}

}
