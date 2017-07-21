package fr.ifremer.octopus.utils;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sdn.vocabulary.implementations.CollectionFactory;
import sdn.vocabulary.interfaces.ICollectionFactory;
import sdn.vocabulary.interfaces.VocabularyException;

public class SDNVocabs {
	static final Logger LOGGER = LogManager.getLogger(SDNVocabs.class.getName());

	private static SDNVocabs sdnVocabs;
	ICollectionFactory cf ;
	
	private SDNVocabs() throws VocabularyException {
		String vocabDir = "resources/vocab";
		File vocab = new File(vocabDir);

		CollectionFactory.newInstance(vocab);
		cf = CollectionFactory.getInstance();
		cf.reload();
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
	
	public void reload(){
		try {
			cf.reload();
		} catch (VocabularyException e) {
			LOGGER.error(e.getMessage());
		}
	}
}
