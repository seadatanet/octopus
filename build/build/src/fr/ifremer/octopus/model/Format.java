package fr.ifremer.octopus.model;

public enum Format {
	MEDATLAS_NON_SDN("MEDATLAS NON SDN", ""),
	MEDATLAS_SDN("MEDATLAS",""),
	ODV_SDN("ODV","txt"),
	CFPOINT("CFPoint","cf");

	String name;
	String extension;
	
	Format(String name, String extension){
		this.name = name;
		this.extension = extension;
	} 
	
	public String getName(){
		return this.name;
	}
	
	public String getExtension(){
		return this.extension;
	}
	
	public boolean isExtensionCompliant(String filename){
		if (this.extension.isEmpty()){
			return true;
		}else{
			return filename.endsWith(this.extension);
		}
	}
}
