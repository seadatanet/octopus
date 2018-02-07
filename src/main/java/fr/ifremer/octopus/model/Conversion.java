package fr.ifremer.octopus.model;

public enum Conversion {
	NONE ("no conversion"),
	MEDATLAS_SDN_TO_CF_POINT("MEDATLAS to CFPoint"),
	MEDATLAS_SDN_TO_ODV_SDN("MEDATLAS to ODV"),
	ODV_SDN_TO_CFPOINT("ODV to CFPoint"),
	CFPOINT_TO_ODV_SDN("CFPoint to ODV"),
	MGD_TO_ODV("MGD to ODV");

	String name;

	Conversion(String name){
		this.name = name;
	} 
	
	public String getName(){
		return this.name;
	}
}
