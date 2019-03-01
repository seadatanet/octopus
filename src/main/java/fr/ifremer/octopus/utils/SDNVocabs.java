package fr.ifremer.octopus.utils;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdn.vocabulary.implementations.CollectionFactory;
import sdn.vocabulary.interfaces.ICollection;
import sdn.vocabulary.interfaces.ICollectionFactory;
import sdn.vocabulary.interfaces.ICollectionMapping;
import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.sismer_tools.csr.CSRListManager;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;

public class SDNVocabs {
	static final Logger LOGGER = LogManager.getLogger(SDNVocabs.class.getName());
	public static final String[] vocabsList = { SdnVocabularyManager.LIST_C17, SdnVocabularyManager.LIST_C77, SdnVocabularyManager.LIST_L05, SdnVocabularyManager.LIST_L22, SdnVocabularyManager.LIST_L23, SdnVocabularyManager.LIST_L33, SdnVocabularyManager.LIST_P01, SdnVocabularyManager.LIST_P02, SdnVocabularyManager.LIST_P06, SdnVocabularyManager.LIST_P09 };

	private static int nonPresent_bodc = -1;
	private static SDNVocabs sdnVocabs;
	ICollectionFactory cf ;
	CSRListManager mgr;
	
	HashMap<String, Integer> oldVersions ;
	HashMap<String, Integer> newVersions;
	private ResourceBundle messages; 
	
	
	private SDNVocabs() throws VocabularyException {
		messages = ResourceBundle.getBundle("bundles/messages", PreferencesManager.getInstance().getLocale());
		oldVersions = new HashMap<>();
		newVersions = new HashMap<>();
		
		String vocabDir = "resources/vocab";
		File vocab = new File(vocabDir);
		try{
			if (cf==null){
			CollectionFactory.newInstance(vocab);
			}
			cf = CollectionFactory.getInstance();
			cf.reload();
		}catch(Exception e){
			LOGGER.error(e.getMessage());
			LOGGER.error("unable to initialize vocabs");
		}
		try {
			mgr = CSRListManager.getInstance("resources/csrlist");
			mgr.checkAndDownloadIfNeeded();
		} catch (Exception e) {
			LOGGER.error("unable to get CSR list");
		} 

	}

	public static SDNVocabs getInstance() throws VocabularyException{
		if (sdnVocabs == null){
			sdnVocabs = new SDNVocabs();
		}
		return sdnVocabs;
	}

	public ICollectionFactory getCf(){
		return cf;
	}
	public CSRListManager getCSRListManager(){
		return mgr;
	}
	public void reload() throws VocabularyException{
		if(!cf.localDirectoryIsBroken())
		{
			try
			{        
				cf.reloadDirectory();                                                                               
			}
			catch(VocabularyException ve)
			{
				LOGGER.error(ve.getMessage());
			}
		}else {
			try {
				cf.reload();
			} catch (VocabularyException e) {
				throw e;
			}
		}
	}
	
	
	public void checkCurrent() throws VocabularyException {
		int version;
		for (String list : SDNVocabs.vocabsList) {
			try {
				version = SDNVocabs.getInstance().getCf().getCollection(false, list).getDescription().getVersion();
				oldVersions.put(list, version);
			} catch (VocabularyException e) {
				LOGGER.info("remove? "+ e.getMessage());
				// if getCollection offLine raises an exception, that means that the list in not in the local directory -> set old version to 0
				oldVersions.put(list, nonPresent_bodc);
				throw e;
			}

		}
	}

	public void readOnlineVersions() throws VocabularyException {
		int version;
		for (String list : SDNVocabs.vocabsList) {
			try {
				version = SDNVocabs.getInstance().getCf().getCollection(true, list).getDescription().getVersion();
				newVersions.put(list, version);
			} catch (VocabularyException e) {
				LOGGER.debug( e.getMessage());
				throw e;
			}

		}
		
	}

	public HashMap<String, Integer>  getOldVersions() {
		return oldVersions;
	}
	public HashMap<String, Integer>  getNewVersions() {
		return newVersions;
	}

	public List<String> getDiff() {
		
		List<String> logMessages= new ArrayList<>();
		try {
			for (String list : SDNVocabs.vocabsList) {

				if (SDNVocabs.getInstance().getOldVersions().get(list).equals(SDNVocabs.getInstance().getNewVersions().get(list))) {
					logMessages.add(MessageFormat.format(messages.getString("preferences.listAlreadyUpToDate"), list, SDNVocabs.getInstance().getNewVersions().get(list)));
				} else {
					int old = SDNVocabs.getInstance().getOldVersions().get(list);
					String oldString;
					if (old == nonPresent_bodc) {
						oldString = "not present";// TODO
					} else {
						oldString = String.valueOf(old);
					}
					logMessages.add(list + ": " + oldString + " -> " + SDNVocabs.getInstance().getNewVersions().get(list));
				}
			}
		} catch (Exception e) {
			LOGGER.debug( e.getMessage());
		}
		return logMessages;
	}
	
	
	public List<String>  updateMappings() throws VocabularyException {
		
		List<String> logMessages= new ArrayList<>();
		ICollection p01 = SDNVocabs.getInstance().getCf().getCollection(true, SdnVocabularyManager.LIST_P01);
		ICollection p02 = SDNVocabs.getInstance().getCf().getCollection(true, SdnVocabularyManager.LIST_P02);
		ICollection p06 = SDNVocabs.getInstance().getCf().getCollection(true, SdnVocabularyManager.LIST_P06);
		ICollection p09 = SDNVocabs.getInstance().getCf().getCollection(true, SdnVocabularyManager.LIST_P09);
		
		LOGGER.debug("mapping P06/P09");
		ICollectionMapping p06_from_P09 = SDNVocabs.getInstance().getCf().getMapping(true, p06, p09.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P09));
		logMessages.add("mapping P06/P09 : ok");
		
		LOGGER.debug("mapping P01/P09");
		ICollectionMapping p01_from_P09 = SDNVocabs.getInstance().getCf().getMapping(true, p01, p09.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P09));
		logMessages.add("mapping P01/P09 : ok");
		
		LOGGER.debug("mapping P01/P02");
		ICollectionMapping p01_from_P02 = SDNVocabs.getInstance().getCf().getMapping(true, p01, p02.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P02));
		logMessages.add("mapping P01/P02 : ok");
		
		/* mapping on list itself (xxx from xxx) is needed to detect deprecated xxx */
		for (String list : SDNVocabs.vocabsList) {
			ICollection collec = SDNVocabs.getInstance().getCf().getCollection(true, list);
			LOGGER.debug("mapping " + list + "/" + list);
			try {
				ICollectionMapping automapping = SDNVocabs.getInstance().getCf().getMapping(true, collec, collec.getMappedDescriptionFromKey(list));
				LOGGER.debug("mapping " + list + "/" + list + ": ok" );
				logMessages.add("mapping " + list + "/" + list + ": ok");
			} catch (Exception e) {
				LOGGER.debug("mapping " + list + "/" + list + " does not exist. BODC terms deprecation checks will not be possible for this list.");
				logMessages.add("mapping " + list + "/" + list + " does not exist. BODC terms deprecation checks will not be possible for this list.");
			}
		}
		return logMessages;
	}
}
