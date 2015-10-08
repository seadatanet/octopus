package fr.ifremer.octopus.utils;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SDNCdiIdObservable  {


	private  StringProperty cdi;
	private  SimpleBooleanProperty selected ;


	public SDNCdiIdObservable(String cdi, boolean selected) {
		this.cdi = new SimpleStringProperty(cdi);
		this.selected = new SimpleBooleanProperty(selected);

	}

	public StringProperty cdiProperty() {
		return cdi;
	}

	public boolean getSelected() {
		return selected.get();
	}

	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}
	public BooleanProperty selectedProperty() {
		return selected;
	}

}
