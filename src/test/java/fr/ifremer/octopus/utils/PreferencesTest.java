package fr.ifremer.octopus.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Assert;
import org.junit.Test;

import fr.ifremer.octopus.controller.OctopusException;

public class PreferencesTest {

	
	@Test
	public void testPreferencesClean()
	{
		try {
			PreferencesManager.getInstance().load();
		} catch (OctopusException e) {
			e.printStackTrace();
		}
		// coupling is disabled
		Assert.assertTrue(!PreferencesManager.getInstance().isCouplingEnabled());
		// edmo code is empty
		Assert.assertTrue(PreferencesManager.getInstance().getEdmoCode().isEmpty());
		// default paths empty
		Assert.assertTrue(PreferencesManager.getInstance().getInputDefaultPath() == null);
		Assert.assertTrue(PreferencesManager.getInstance().getOutputDefaultPath() == null);
		Assert.assertTrue(PreferencesManager.getInstance().getCouplingPrefix().isEmpty());
		
		//default language is french
		Assert.assertTrue(PreferencesManager.getInstance().getLocale() == PreferencesManager.LOCALE_UK);
		
	}
//	@Test
	public void test() {
		JAXBContext context;
		try {
			
			FileOutputStream o = new FileOutputStream(new File("preferences.xml"));
			context = JAXBContext.newInstance(Preferences.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			Preferences object = new Preferences();
			object.setLanguage("fr");
			object.setInputDefaultPath("input");
			object.setOutputDefaultPath("output");

			m.marshal(object, o);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		
	}

}
