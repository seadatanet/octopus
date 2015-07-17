package fr.ifremer.octopus.model;

public enum Format {

	MEDATLAS_SDN("medatlas"),
	ODV_SDN("odv"),
	CFPOINT("cfpoint"),
	UNKNOWN("unknown");

	String name;

	Format(String name){
		this.name = name;
	} 
	
	public String getName(){
		return this.name;
	}
}
