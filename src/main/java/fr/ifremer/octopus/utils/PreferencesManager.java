package fr.ifremer.octopus.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreferencesManager {


	private static final Logger LOGGER = LogManager.getLogger(PreferencesManager.class);
	private static final String preferencesFile = "resources/preferences.xml";
	private static PreferencesManager mgr;

	private Preferences preferences;

	public static PreferencesManager getInstance(){
		if (mgr==null){
			mgr = new PreferencesManager();
		}
		return mgr;
	}

	private PreferencesManager() {
	}

	public void load(){
		try {
			JAXBContext jc = JAXBContext.newInstance(Preferences.class);
			Unmarshaller u = jc.createUnmarshaller();

			File f = new File(preferencesFile);
			preferences = (Preferences) u.unmarshal(f);

		} catch (JAXBException e) {
			LOGGER.error(e.getMessage());
		}
	}


	public Locale getLocale(){
		Locale locale;
		switch (preferences.getLanguage()) {
		case "fr":
			locale = Locale.FRANCE;
			break;
		case "en":
			locale = Locale.ENGLISH;
			break;
		default:
			locale = Locale.ENGLISH;
			break;
		}

		return locale;
	}
	public void save(){
		JAXBContext context;
		try {

			FileOutputStream o = new FileOutputStream(new File(preferencesFile));
			context = JAXBContext.newInstance(Preferences.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(preferences, o);
		} catch (JAXBException e) {
			LOGGER.error("Error while writing preferences.");// TODO
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			LOGGER.error("can not find preferences files.");// TODO
			e.printStackTrace();
		}
	}

	public void setLocale(Locale language) {
		if (language== Locale.FRANCE){
			preferences.setLanguage("fr");
		}else{
			preferences.setLanguage("en");
		}
	}

	public void setLocale(String newValue) {
		preferences.setLanguage(newValue);
		
	}

	public String getInputDefaultPath(){
		File f = new File(preferences.getInputDefaultPath());
		if (f.exists()){
			return preferences.getInputDefaultPath();
		}else{
			return null;
		}
	}
	public String getOutputDefaultPath(){
		File f = new File(preferences.getOutputDefaultPath());
		if (f.exists()){
			return preferences.getOutputDefaultPath();
		}else{
			return null;
		}
	}
	public void setInputDefaultPath(String inputDefaultPath){
		preferences.setInputDefaultPath(inputDefaultPath);
	}
	public void setOutputDefaultPath(String outputDefaultPath){
		preferences.setOutputDefaultPath(outputDefaultPath);
	}

}
