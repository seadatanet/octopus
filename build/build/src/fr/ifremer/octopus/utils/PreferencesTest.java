package fr.ifremer.octopus.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;

public class PreferencesTest {

	@Test
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

}
