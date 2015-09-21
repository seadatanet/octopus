package fr.ifremer.octopus.model;

public enum Format {
	MEDATLAS_NON_SDN("MEDATLAS NON SDN", "","med"),
	MEDATLAS_SDN("MEDATLAS","", "med"),
	ODV_SDN("ODV","txt","txt"),
	CFPOINT("CFPoint","cf","cf");

	String name;
	/**
	 * checked for input files
	 */
	String mandatoryExtension;
	/**
	 * extension used for output files
	 */
	String outExtension;
	
	Format(String name, String mandatoryExtension, String outExtension){
		this.name = name;
		this.mandatoryExtension = mandatoryExtension;
		this.outExtension = outExtension;
	} 
	
	public String getName(){
		return this.name;
	}
	
	public String getMandatoryExtension(){
		return this.mandatoryExtension;
	}
	
	public String getOutExtension() {
		return outExtension;
	}

	public boolean isExtensionCompliant(String filename){
		if (this.mandatoryExtension.isEmpty()){
			return true;
		}else{
			return filename.endsWith(this.mandatoryExtension);
		}
	}
}
