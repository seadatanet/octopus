package fr.ifremer.octopus.controller.checker;

import java.io.File;

import fr.ifremer.sismer_tools.seadatanet.Format;


public abstract  class FormatChecker {

	public FormatChecker() {
	}

	public abstract Format check(File f) throws Exception ;

}
