package mx.com.insigniait.instalador_sat.dto;

import java.util.Date;

public class Pfx {
	
	private String path;
	private String serial;
	private String alias;
	private String email;
	private String purposes;
	private String publicKey;
	
	private Date startDate;
	private Date endData;
	
	private Issuer issuer;
	private Subject subject;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPurposes() {
		return purposes;
	}

	public void setPurposes(String purposes) {
		this.purposes = purposes;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndData() {
		return endData;
	}

	public void setEndData(Date endData) {
		this.endData = endData;
	}

	public Issuer getIssuer() {
		return issuer;
	}

	public void setIssuer(Issuer issuer) {
		this.issuer = issuer;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}
	
	public class Issuer {
		
		private String hash;
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
	}
	
	public class Subject {

		private String commonName;
		private String name;
		private String organization;
		private String country;
		private String email;
		private String id;
		private String serialNumber;
    	private String hash;
    	
		public String getCommonName() {
			return commonName;
		}
		
		public void setCommonName(String commonName) {
			this.commonName = commonName;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getOrganization() {
			return organization;
		}
		
		public void setOrganization(String organization) {
			this.organization = organization;
		}
		
		public String getCountry() {
			return country;
		}
		
		public void setCountry(String country) {
			this.country = country;
		}

		public String getEmail() {
			return email;
		}
		
		public void setEmail(String email) {
			this.email = email;
		}
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getSerialNumber() {
			return serialNumber;
		}
		
		public void setSerialNumber(String serialNumber) {
			this.serialNumber = serialNumber;
		}
		
		public String getHash() {
			return hash;
		}
		
		public void setHash(String hash) {
			this.hash = hash;
		}
	}
}
