package fr.ifremer.octopus.controller.checker;

import java.io.File;


public abstract  class FormatChecker {

	public FormatChecker() {
	}

	public abstract void check(File f) throws Exception ;

}
