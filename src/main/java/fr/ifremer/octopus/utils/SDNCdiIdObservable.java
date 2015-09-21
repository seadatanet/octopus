package fr.ifremer.octopus.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import fr.ifremer.seadatanet.splitter.bean.SdnCDIId;

public class SDNCdiIdObservable  {


	private final StringProperty cdi;

	public SDNCdiIdObservable(SdnCDIId sdnCdi) {
		this.cdi = new SimpleStringProperty(sdnCdi.getLocalCdiId());

	}
	public SDNCdiIdObservable(String cdi) {
		this.cdi = new SimpleStringProperty(cdi);

	}
	
	public StringProperty cdiProperty() {
        return cdi;
    }
}
