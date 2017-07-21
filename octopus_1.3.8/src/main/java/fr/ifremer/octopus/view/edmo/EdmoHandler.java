package fr.ifremer.octopus.view.edmo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class EdmoHandler
 * 
 * @author Herve Taniou
 */
public class EdmoHandler extends DefaultHandler {

	private static String n_code = "n_code"; //$NON-NLS-1$
	private static String name = "name"; //$NON-NLS-1$
	private static String organisation_exists = "organisation_exists";
	private static String c_country = "c_country";

	private final java.util.HashMap<Integer, String> _valeurs = new HashMap<Integer, String>();
	private static HashMap<Integer, EdmoEntity> edmoList = new HashMap<Integer, EdmoEntity>();
	private Integer intCode;
	private String entityName;
	private String orgExist;
	// buffer nous permettant de récupérer les données
	private StringBuffer buffer;


	public static ArrayList<String> listeOrigine;

	public static ArrayList<String> listeFiltree;

	public HashMap<Integer, String> get_valeurs() {
		return _valeurs;
	}

	public static HashMap<Integer, EdmoEntity> getEdmoList() {
		return edmoList;
	}
	public static String getEdmoName(int edmo_code){
		return edmoList.get(edmo_code).name;
	}
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {

		if (qName.equals(n_code)) {
			buffer = new StringBuffer();
		} else if (qName.equals(name)) {
			buffer = new StringBuffer();
		} else if (qName.equals(organisation_exists)) {
			buffer = new StringBuffer();
		} else if (qName.equals(c_country)) {
			buffer = new StringBuffer();
		}

	}

	@Override
	public void endElement(String uri, String localName, String qName) {

		if (qName.equals(n_code)) {
			intCode = Integer.parseInt(buffer.toString());
			buffer = null;
		} else if (qName.equals(name)) {
			// _valeurs.put(intCode, buffer.toString());
			entityName = buffer.toString();
			buffer = null;
		} else if (qName.equals(organisation_exists)) {
			// edmoList.add(new EdmoEntity(intCode, entityName,
			// buffer.toString()));
			orgExist = getOrgExistLabel(buffer.toString());
			buffer = null;
		} else if (qName.equals(c_country)) {
			edmoList.put(intCode, new EdmoEntity(intCode, entityName, orgExist, buffer.toString()));
			buffer = null;
		}
	}

	private String getOrgExistLabel(String xmlValue) {
		if (xmlValue.equals("True")) {
			return "Yes";
		} else {
			return "No";
		}

	}

	@Override
	public void characters(char[] ch, int start, int length) {

		if (buffer != null) {
			String lecture = new String(ch, start, length);
			buffer.append(lecture);
		}
	}

}
