package fr.ifremer.octopus.utils;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdn.vocabulary.implementations.CollectionFactory;
import sdn.vocabulary.interfaces.ICollectionFactory;
import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.sismer_tools.csr.CSRListManager;

public class SDNVocabs {
	static final Logger LOGGER = LogManager.getLogger(SDNVocabs.class.getName());

	private static SDNVocabs sdnVocabs;
	ICollectionFactory cf ;
	CSRListManager mgr;
	private SDNVocabs() throws VocabularyException {
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
				System.out.println("ERROR : "+ve.getMessage());
			}
		}else {
			try {
				cf.reload();
			} catch (VocabularyException e) {
				throw e;
			}
		}
	}
}
