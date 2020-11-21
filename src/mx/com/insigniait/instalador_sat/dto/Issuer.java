package mx.com.insigniait.instalador_sat.dto;

public class Issuer {
	
	private String hash;
	private String name;
	private String serialNumber;
	private String commonName;
	private String organization;
	private String organizationUnit;
	private String email;
	private String street;
	private String postalCode;
	private String country;
	private String state;
	private String locality;
	private String id;
	private String unstructuredName;
	
	public String getHash() {
		return hash;
	}
	
	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public String getCommonName() {
		return commonName;
	}
	
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	
	public String getOrganization() {
		return organization;
	}
	
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	
	public String getOrganizationUnit() {
		return organizationUnit;
	}
	
	public void setOrganizationUnit(String organizationUnit) {
		this.organizationUnit = organizationUnit;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getStreet() {
		return street;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	
	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getLocality() {
		return locality;
	}
	
	public void setLocality(String locality) {
		this.locality = locality;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUnstructuredName() {
		return unstructuredName;
	}
	
	public void setUnstructuredName(String unstructuredName) {
		this.unstructuredName = unstructuredName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
}
