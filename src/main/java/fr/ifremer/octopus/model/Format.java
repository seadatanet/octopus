package fr.ifremer.octopus.model;

public enum Format {
	MEDATLAS_NON_SDN("MEDATLAS NON SDN"),
	MEDATLAS_SDN("MEDATLAS"),
	ODV_SDN("ODV"),
	CFPOINT("CFPoint");

	String name;

	Format(String name){
		this.name = name;
	} 
	
	public String getName(){
		return this.name;
	}
}
