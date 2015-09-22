package fr.ifremer.octopus.controller.checker;

import java.io.File;

import fr.ifremer.octopus.model.Format;

public abstract  class FormatChecker {

	public FormatChecker() {
	}

	public abstract void check(File f) throws Exception ;

}
