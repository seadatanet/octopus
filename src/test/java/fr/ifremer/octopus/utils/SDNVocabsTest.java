package fr.ifremer.octopus.utils;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import sdn.vocabulary.interfaces.ICollectionFactory;
import sdn.vocabulary.interfaces.IElement;
import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.seadatanet.odv.utils.Vocabulary;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;

public class SDNVocabsTest {
	static final Logger LOGGER = LogManager.getLogger(SDNVocabsTest.class.getName());
	
	
	
//	@Test
//	@Ignore
	public void test() {
		int nonPresent_bodc=-1;

		try{
			int before = EdmoManager.getInstance().getEdmoList().size();
			EdmoManager.getInstance().updateEdmo();
			int after = EdmoManager.getInstance().getEdmoList().size();
			LOGGER.info("edmo codes number: "+ before + " -> "+ after+System.getProperty("line.separator"));
		}catch(Exception e){
			
		}
		try{
			String[] vocabsList = {
					SdnVocabularyManager.LIST_P01, SdnVocabularyManager.LIST_P06, SdnVocabularyManager.LIST_P09,
					SdnVocabularyManager.LIST_L22, SdnVocabularyManager.LIST_L33};
			int version;
			HashMap<String, Integer> oldVersions = new HashMap<>();
			HashMap<String, Integer> newVersions = new HashMap<>();
			for (String list: vocabsList){
				try {
					version = SDNVocabs.getInstance().getCf().getCollection(false, list).getDescription().getVersion();
					oldVersions.put(list, version );
				} catch (VocabularyException e) {
					LOGGER.error(e.getMessage());
					LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
					// if getCollection offLine raises an exception, that means that the list in not in the local directory -> set old version to 0
					oldVersions.put(list, nonPresent_bodc );
				}

			}

			// reload
			try{
				SDNVocabs.getInstance().reload();
			}catch(Exception e){
				LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
			}

			// get collections online
			for (String list: vocabsList){
				try {
					version = SDNVocabs.getInstance().getCf().getCollection(true, list).getDescription().getVersion();
					newVersions.put(list, version );
				} catch (VocabularyException e) {
					LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
				}

			}


			try{
				for (String list: vocabsList){

					if (oldVersions.get(list).equals(newVersions.get(list))){
						LOGGER.info(list + ": already up to date (version "+  newVersions.get(list) + ")"+System.getProperty("line.separator") );
					}else{
						 int old=oldVersions.get(list) ;
						 String oldString;
						 if (old==nonPresent_bodc){
							 oldString = "not present";// TODO
						 }else{
							 oldString=String.valueOf(old);
						 }
						 LOGGER.info(list + ": " +oldString+ " -> " + newVersions.get(list)+System.getProperty("line.separator") );
					}
				}
			}catch(Exception e){
				LOGGER.error(e.getMessage());
				LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
			}
		}catch(Exception e){
			LOGGER.error(e.getMessage());
			LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
		}


	}

}
