package fr.ifremer.octopus.utils;

import java.io.File;

import sdn.vocabulary.implementations.CollectionFactory;
import sdn.vocabulary.interfaces.ICollectionFactory;
import sdn.vocabulary.interfaces.VocabularyException;
import fr.ifremer.sismer_tools.seadatanet.SdnVocabularyManager;

public class SDNVocabs {

	private static SDNVocabs sdnVocabs;
	ICollectionFactory cf ;
	
	private SDNVocabs() throws VocabularyException {
		String vocabDir = "resources/vocab";
		File vocab = new File(vocabDir);

		CollectionFactory.newInstance(vocab);
		cf = CollectionFactory.getInstance();
		cf.reload();
		SdnVocabularyManager vocabMgr  = new SdnVocabularyManager(cf);
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
}
