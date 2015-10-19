package fr.ifremer.octopus.utils;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Preferences {

	private String language;
	private String inputDefaultPath;
	private String outputDefaultPath;
	private String edmocode;
	private String couplingPrefix;
	private boolean isCouplingEnabled;
	
	public String getEdmocode() {
		return edmocode;
	}
	public void setEdmocode(String edmocode) {
		this.edmocode = edmocode;
	}
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
	public String getCouplingPrefix() {
		return couplingPrefix;
	}
	public void setCouplingPrefix(String couplingPrefix) {
		this.couplingPrefix = couplingPrefix;
	}
	public void setCouplingEnabled(boolean enabled) {
		isCouplingEnabled=enabled;
		
	}
	public boolean isCouplingEnabled() {
		return isCouplingEnabled;
	}

}
