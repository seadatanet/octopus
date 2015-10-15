package fr.ifremer.octopus.view.edmo;

public class EdmoEntity {
	Integer code;
	String name;
	String orgExist;
	String country;

	public Integer getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getOrgExist() {
		return orgExist;
	}

	public String getCountry() {
		return country;
	}

	public EdmoEntity(int code, String name, String orgExist, String country) {
		this.code = code;
		this.name = name;
		this.orgExist = orgExist;
		this.country = country;
	}
}
