package fr.ifremer.octopus.utils;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Preferences {

	private String language;
	private String inputDefaultPath;
	private String outputDefaultPath;
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getInputDefaultPath() {
		return inputDefaultPath;
	}
	public void setInputDefaultPath(String inputDefaultPath) {
		this.inputDefaultPath = inputDefaultPath;
	}
	public String getOutputDefaultPath() {
		return outputDefaultPath;
	}
	public void setOutputDefaultPath(String outputDefaultPath) {
		this.outputDefaultPath = outputDefaultPath;
	}

}
