package fr.ifremer.octopus.controller.couplingTable;

import java.time.LocalDateTime;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import fr.ifremer.sismer_tools.coupling.CouplingRecord;

public class CouplingRecordFx {
	private final StringProperty local_cdi_id;
	private final IntegerProperty modus;
	private final StringProperty format;
	private final StringProperty path;
	private final ObjectProperty<LocalDateTime> date;
	
	
	
	public CouplingRecordFx(CouplingRecord cr) {
		this.local_cdi_id = new SimpleStringProperty(cr.getLocal_cdi_id());
		this.modus = new SimpleIntegerProperty(cr.getModus());
		this.format = new SimpleStringProperty(cr.getFormat().toCouplingFormat());//28748
		this.path = new SimpleStringProperty(cr.getPath());
		this.date = new SimpleObjectProperty<LocalDateTime>(cr.getDate().toLocalDateTime());
	}



	public StringProperty getLocal_cdi_id() {
		return local_cdi_id;
	}



	public IntegerProperty getModus() {
		return modus;
	}



	public StringProperty getFormat() {
		return format;
	}



	public StringProperty getPath() {
		return path;
	}



	public ObjectProperty<LocalDateTime> getDate() {
		return date;
	}

}
