package mx.com.insigniait.instalador_sat.dto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Pfx {
	
	private String path;
	private String serial;
	private String alias;
	private String email;
	private String purposes;
	private String publicKey;
	private String fingerprint;
	
	private Date startDate;
	private Date endDate;
	
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
		if(alias.equals("<No Alias>")) {
			if(this.getSubject() != null) {
				return this.getSubject().getCommonName();
			}
			else {
				return "";
			}
		}
		else {
			return alias;
		}
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
	
	public void setPurposesFromYesNoCommaSeparatedList(String purposes) {
		BufferedReader bufReader = new BufferedReader(new StringReader(purposes));
		
		String 	line;
		String 	aux = "";
		
		try {
			while((line = bufReader.readLine()) != null) {
				if(line.contains("Yes")) {
					aux += line.substring(0, line.indexOf(":") - 1) + ", ";
				}
			}
		} 
		catch (IOException e) {	e.printStackTrace();	}
		
		aux = aux.substring(0, aux.length() - 2);
		
		this.purposes = aux;
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
	
	public void setStartDate(String startDate, String pattern) {
		SimpleDateFormat sdf =  new SimpleDateFormat(pattern);
		
		try {
			this.startDate = sdf.parse(startDate);
		} catch (ParseException e) {
			System.err.println(startDate);
		}
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public void setEndDate(String endDate, String pattern) {
		SimpleDateFormat sdf =  new SimpleDateFormat(pattern);
		
		try {
			this.endDate = sdf.parse(endDate);
		} catch (ParseException e) {
			System.err.println(endDate);
		}
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
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
}
