package fr.ifremer.octopus.utils;

import java.io.File;
import java.io.FileInputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fr.ifremer.sismer_tools.externalresources.ExternalResourcesManager;

public class SDNVocabsTest {
	static final Logger LOGGER = LogManager.getLogger(SDNVocabsTest.class.getName());
	
	
	
	@Before
	public void before(){
		try {
			FileInputStream extResourcesConf = new FileInputStream(new File("resources/externalResourcesConfiguration.yaml"));
			ExternalResourcesManager.getInstance(extResourcesConf, "resources/externalResources");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	@Ignore
	public void reloadBODCVocabs() {
		try {
			ExternalResourcesManager.getInstance().deleteAndReload();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
//	@Test
//	@Ignore
//	public void test() {
//		int nonPresent_bodc=-1;
//
//		try{
//			int before = EdmoManager.getInstance().getEdmoList().size();
//			EdmoManager.getInstance().updateEdmo();
//			int after = EdmoManager.getInstance().getEdmoList().size();
//			LOGGER.info("edmo codes number: "+ before + " -> "+ after+System.getProperty("line.separator"));
//		}catch(Exception e){
//			
//		}
//		try{
//			
//			
//			// lists
//			String[] vocabsList = {
//					SdnVocabularyManager.LIST_P01, SdnVocabularyManager.LIST_P02, 
//					SdnVocabularyManager.LIST_P06, SdnVocabularyManager.LIST_P09,
//					SdnVocabularyManager.LIST_L22, SdnVocabularyManager.LIST_L33, 
//					SdnVocabularyManager.LIST_L23,
//					SdnVocabularyManager.LIST_C17,	SdnVocabularyManager.LIST_C77,
//					SdnVocabularyManager.LIST_L05};
//		
//			int version;
//			HashMap<String, Integer> oldVersions = new HashMap<>();
//			HashMap<String, Integer> newVersions = new HashMap<>();
//			LOGGER.info("check current vocabulary files");
//			for (String list: vocabsList){
//				try {
//					version = SDNVocabs.getInstance().getCf().getCollection(false, list).getDescription().getVersion();
//					oldVersions.put(list, version );
//				} catch (VocabularyException e) {
//					LOGGER.info(e.getMessage());
//					LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
//					// if getCollection offLine raises an exception, that means that the list in not in the local directory -> set old version to 0
//					oldVersions.put(list, nonPresent_bodc );
//				}
//
//			}
//
//			
//			// reload
//			try{
//				LOGGER.info("download or update vocabulary files");
//				SDNVocabs.getInstance().reload();
//			}catch(Exception e){
//				LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
//			}
//
//			// get collections online
//			for (String list: vocabsList){
//				try {
//					version = SDNVocabs.getInstance().getCf().getCollection(true, list).getDescription().getVersion();
//					newVersions.put(list, version );
//				} catch (VocabularyException e) {
//					LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
//				}
//
//			}
//
//			
//			
//			try{
//				for (String list: vocabsList){
//
//					if (oldVersions.get(list).equals(newVersions.get(list))){
//						LOGGER.info(list + ": already up to date (version "+  newVersions.get(list) + ")"+System.getProperty("line.separator") );
//					}else{
//						 int old=oldVersions.get(list) ;
//						 String oldString;
//						 if (old==nonPresent_bodc){
//							 oldString = "not present";// TODO
//						 }else{
//							 oldString=String.valueOf(old);
//						 }
//						 LOGGER.info(list + ": " +oldString+ " -> " + newVersions.get(list)+System.getProperty("line.separator") );
//					}
//				}
//			}catch(Exception e){
//				LOGGER.error(e.getMessage());
//				LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
//			}
//			
//			//mappings
//			LOGGER.info("update mapping files");
//			ICollection p06 = SDNVocabs.getInstance().getCf().getCollection(true, "P06");
//			ICollection p09 = SDNVocabs.getInstance().getCf().getCollection(true, "P09");
//			ICollection p02 = SDNVocabs.getInstance().getCf().getCollection(true, "P02");
//			ICollection p01 = SDNVocabs.getInstance().getCf().getCollection(true, "P01");
//			
////			try {
////			ICollectionMapping p06_from_P09 = SDNVocabs.getInstance().getCf().getMapping(true, p06, p09.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P09));
////			ICollectionMapping p01_from_P09 = SDNVocabs.getInstance().getCf().getMapping(true, p01, p09.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P09));
////			ICollectionMapping p01_from_P02 = SDNVocabs.getInstance().getCf().getMapping(true, p01, p02.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P02));
////			} catch (VocabularyException e) {
////				LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
////			}
////			
//			
//			try {
//				LOGGER.debug("mapping P06/P09");
//				ICollectionMapping p06_from_P09 = SDNVocabs.getInstance().getCf().getMapping(true, p06, p09.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P09));
//				LOGGER.info("mapping P06/P09 : ok");
//			
//				LOGGER.debug("mapping P01/P09");
//				ICollectionMapping p01_from_P09 = SDNVocabs.getInstance().getCf().getMapping(true, p01, p09.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P09));
//				LOGGER.info("mapping P01/P09 : ok");
//				
//				LOGGER.debug("mapping P01/P02");
//				ICollectionMapping p01_from_P02 = SDNVocabs.getInstance().getCf().getMapping(true, p01, p02.getMappedDescriptionFromKey(SdnVocabularyManager.LIST_P02));
//				LOGGER.info("mapping P01/P02 : ok");
//				/*mapping on list itself (xxx from xxx) is needed to detect deprecated xxx */
//				for (String list: vocabsList){
//					ICollection collec = SDNVocabs.getInstance().getCf().getCollection(true, list);
//					LOGGER.debug("mapping "+list+"/"+list);
//					try{
//						ICollectionMapping automapping = SDNVocabs.getInstance().getCf().getMapping(true, collec, collec.getMappedDescriptionFromKey(list));
//						LOGGER.info("mapping "+list+"/"+list+": ok" );
//						LOGGER.debug("mapping "+list+"/"+list+": ok" );
//					}catch(Exception e){
//						LOGGER.warn("mapping "+list+"/"+list + " does not exist. BODC tems deprecation checks will not be possible for this list.");
//						LOGGER.info("mapping "+list+"/"+list + " does not exist. BODC tems deprecation checks will not be possible for this list.");
//					}
//				}
//			} catch (VocabularyException e) {
//				LOGGER.info(e.getMessage());
//				LOGGER.info(e.getMessage());
//			}finally{
//			}
//			
//		}catch(Exception e){
//			LOGGER.error(e.getMessage());
//			LOGGER.info(e.getMessage()+System.getProperty("line.separator"));
//		}
//
//
//	}

}
