package mx.com.insigniait.instalador_sat.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import mx.com.insigniait.instalador_sat.dto.Issuer;
import mx.com.insigniait.instalador_sat.dto.Pfx;
import mx.com.insigniait.instalador_sat.dto.Subject;

public class OpenSsl  {
	
    private static File 	directory 	= 	new File(System.getProperty("java.io.tmpdir"));
    private static boolean 	isWindows 	=	System.getProperty("os.name").toLowerCase().startsWith("windows");
    
    private static int 		exitCode;
    private static Process	process;
    private static String 	comando;
    private static ProcessBuilder builder;
    
    public static File crtToPem(File certificate)  {

    	assertEnviroment();
    	
    	String nombrePem = Long.toString(System.currentTimeMillis()) + "_CRT.pem";
    	
    	comando 	=  	"openssl x509 -inform der -in \"{rutaCer}\" -out {nombrePem}";
    	comando 	=	comando.replace("{rutaCer}", certificate.getPath()).replace("{nombrePem}", nombrePem);
    	
    	callCommand(comando);
    	
    	return new File(directory.getPath() + "/" + nombrePem);
    }
    
    public static File keyToPem(File key, String contraseña)  {
    	
    	assertEnviroment();
    	
    	String nombrePem = Long.toString(System.currentTimeMillis()) + "_KEY.pem";

    	comando 	= 	"openssl pkcs8 -inform der -in \"{rutaKey}\" -passin pass:\"{contraseña}\" -out {nombrePem}";
    	comando 	=	comando.replace("{rutaKey}", key.getPath()).replace("{nombrePem}", nombrePem).replace("{contraseña}", contraseña);
    	
    	callCommand(comando);
    	
    	return new File(directory.getPath() + "/" + nombrePem);
    }
    
    public static File pemsToPfx(File cerPem, File keyPem, String contraseña)  {
    	
    	assertEnviroment();
    	
    	String nombrePfx = Long.toString(System.currentTimeMillis()) + "_CERTIFICATE.pfx";

    	comando 	=  	"openssl pkcs12 -export -in \"{rutaCer}\" -inkey \"{rutaKey}\" -out {nombrePfx} -password pass:\"{contraseña}\"";
    	comando 	=	comando.replace("{rutaCer}", cerPem.getPath()).replace("{rutaKey}", keyPem.getPath());
		comando 	=	comando.replace("{nombrePfx}", nombrePfx).replace("{contraseña}", contraseña);
    	
    	callCommand(comando);
    	
    	return new File(directory.getPath() + "/" + nombrePfx);
    }
    
    public static void installCertificate(File certificate, String contraseña)  {
    	
    	assertEnviroment();
    	
    	comando 	=  	"certutil -f -silent -user -p \"{contraseña}\" -importpfx \"{rutaCer}\" NoRoot";
    	comando 	=	comando.replace("{rutaCer}", certificate.getPath()).replace("{contraseña}", contraseña);
    	
    	callCommand(comando);
    }
    
    /*
     * Costosa si se usa mucho, la verdad
     */
    public static Pfx retrievePfxProperty(File pfx, String contraseña) {
    	
    	assertEnviroment();
    	
    	Pfx 		certificate 	= 	new Pfx();
    	Subject 	subject 		= 	new Subject();
    	Issuer 		issuer 			=	new Issuer();
    	String 		response;

    	//Datos generales
    	String[] 	props 	= 	{"alias", "serial", "email", "purpose", "pubkey", "fingerprint", "startdate", "enddate", "subject_hash", "issuer_hash"};

    	comando 	=  	"openssl pkcs12 -in \"{pfx}\" -passin pass:\"{contraseña}\" | openssl x509 -noout -{prop}";
    	comando 	=	comando.replace("{pfx}", pfx.getPath()).replace("{contraseña}", contraseña);

    	for (String prop : props) {
    		response 	= 	callCommand(comando.replace("{prop}", prop), true);
    		
    		switch(prop) {
	    		case "alias":
	    			certificate.setAlias(response);
	    		case "fingerprint":
	    			certificate.setFingerprint(response.replace("SHA1 Fingerprint=", ""));
	    			break;
	    		case "purpose":
	    			certificate.setPurposesFromYesNoCommaSeparatedList(response);
	    			break;
	    		case "serial":
	    			certificate.setSerial(response.replace("serial=", ""));
	    			break;
	    		case "startdate":
	    			certificate.setStartDate(response.replace("notBefore=", ""), "MMM dd H:m:s yyyy z");
	    			break;
	    		case "enddate":
	    			certificate.setEndDate(response.replace("notAfter=", ""), "MMM dd H:m:s yyyy z");
	    			break;
	    		case "email":
	    			certificate.setEmail(response);
	    			break;
	    		case "pubkey":
	    			certificate.setPublicKey(response);
	    			break;
	    		case "subject_hash": 
	    			subject.setHash(response);
	    			break;
	    		case "issuer_hash":
	    			issuer.setHash(response);
	    			break;
    		}
		}
    	
    	//Datos del sujeto
		response 	= 	callCommand(comando.replace("{prop}", "subject"), true);
		response 	=	Util.replaceUTF8HexChars(response).replace("subject=", "");

		//Separar respuesta por comas, respetando respuestas que tengan comas dentro de comillas
		Map<String, String>	mapaSubject = Util.createMapFromCommaSeparatedString(response);
		
		for (Entry<String, String> prop : mapaSubject.entrySet()) {
			switch(prop.getKey()) {
				case "CN":
					subject.setCommonName(prop.getValue());
					break;
				case "name":
					subject.setName(prop.getValue());
					break;
				case "O":
					subject.setOrganization(prop.getValue());
					break;
				case "C":
					subject.setCountry(prop.getValue());
					break;
				case "emailAddress":
					subject.setEmail(prop.getValue());
					break;
				case "x500UniqueIdentifier":
					subject.setId(prop.getValue());
					break;
				case "serialNumber":
					subject.setSerialNumber(prop.getValue());
			}
		}
		
		//Datos del emisor
		response 	= 	callCommand(comando.replace("{prop}", "issuer"), true);
		response 	=	Util.replaceUTF8HexChars(response).replace("issuer=", "");
    		
		//Separar respuesta por comas, respetando respuestas que tengan comas dentro de comillas
		Map<String, String>	mapaIssuer = Util.createMapFromCommaSeparatedString(response);
    	
		for (Entry<String, String> prop : mapaIssuer.entrySet()) {
			switch(prop.getKey()) {
				case "CN":
					issuer.setCommonName(prop.getValue());
					break;
				case "N":
					issuer.setName(prop.getValue());
					break;
				case "O":
					issuer.setOrganization(prop.getValue());
					break;
				case "OU":
					issuer.setOrganizationUnit(prop.getValue());
					break;
				case "emailAddress":
					issuer.setEmail(prop.getValue());
					break;
				case "street":
					issuer.setStreet(prop.getValue());
					break;
				case "postalCode":
					issuer.setPostalCode(prop.getValue());
					break;
				case "C":
					issuer.setCountry(prop.getValue());
					break;
				case "ST":
					issuer.setState(prop.getValue());
					break;
				case "L":
					issuer.setLocality(prop.getValue());
					break;
				case "x500UniqueIdentifier":
					issuer.setId(prop.getValue());
					break;
				case "serialNumber":
					issuer.setSerialNumber(prop.getValue());
					break;
				case "unstructuredName":
					issuer.setUnstructuredName(prop.getValue());
			}
		}
		
		certificate.setIssuer(issuer);
    	certificate.setSubject(subject);	
		
    	return certificate;
    }
    
    public String getComando() {
    	return builder != null ? Util.join(builder.command(), null) : "";
    }
    
    private static void assertEnviroment() {
    	if(!isWindows) {
    		throw new IllegalStateException("La aplicación solo es ejecutable en Windows.");
    	}
    }
    
    private static void callCommand(String command) {
    	callCommand(command, false);
    }

    private static String callCommand(String command, Boolean returnResponse) {
    	
    	builder = new ProcessBuilder();
    	
    	builder.command("cmd.exe", "/c", command);
    	builder.directory(directory);
    	
    	try {
    		process = builder.start();
    		
    		try {
    			exitCode = process.waitFor();
    			
    			if(exitCode != 0) {
    				String message = new BufferedReader(new InputStreamReader(process.getErrorStream())).lines().collect(Collectors.joining("\n"));
    				
    				if(message.startsWith("Error decrypting key")) {
    					System.err.println(message + "\n");
    					message = "Contraseña incorrecta";
    				}

    				throw new IllegalStateException(message);
    			}
    			else {
    				if(returnResponse) {
    					String message =  new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.joining("\n"));
    					return message;
    				}
    				else {
    					return null;
    				}
    			}
    		} 
    		catch (InterruptedException e) {	
    			throw new IllegalStateException("Error interno. Contacte al administrador: \nInterruptedException: " + e.getMessage());
    		}
    		finally {
    			process.destroy();
    		}
    	} 
    	catch (NullPointerException e) {	
    		throw new IllegalStateException("Error interno. Contacte al administrador: \nNullPointerException: " + e.getMessage());
    	}
		catch (IndexOutOfBoundsException e) {	
			throw new IllegalStateException("Error interno. Contacte al administrador: \nIndexOutOfBoundsException: " + e.getMessage());
		}
		catch (SecurityException e) {	
    		throw new IllegalStateException("Error interno. Contacte al administrador: \nSecurityException: " + e.getMessage());
    	} 
    	catch (IOException e) {
    		throw new IllegalStateException("Error interno. Contacte al administrador: \nIOException: " + e.getMessage());
		}
    	finally {
    		System.out.println("[COMMAND] " + Util.join(builder.command(), null));
    	}
    }
}
