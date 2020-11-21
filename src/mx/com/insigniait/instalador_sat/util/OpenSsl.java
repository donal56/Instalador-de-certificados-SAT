package mx.com.insigniait.instalador_sat.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.stream.Collectors;

public class OpenSsl  {
	
    private static File 	directory 	= 	new File(System.getProperty("java.io.tmpdir"));
    private static boolean 	isWindows 	=	System.getProperty("os.name").toLowerCase().startsWith("windows");
    
    private static int 		exitCode;
    private static Process	process;
    private static String 	comando;
    private static ProcessBuilder builder;
    
	public static enum CERTIFICATE_PROPERTY { 	
		SERIAL, 
		ALIAS, 
		EMAIL, 
		PURPOSE, 
		PUBKEY, 
		FINGERPRINT,
		
		STARTDATE, 
		ENDDATE, 
		
		SUBJECT_HASH, 
    	SUBJECT_COMMON_NAME,
    	SUBJECT_NAME,
    	SUBJECT_ORGANIZATION,
    	SUBJECT_COUNTRY,
    	SUBJECT_EMAIL,
    	SUBJECT_ID,
    	SUBJECT_SERIAL_NUMBER,
    	
    	ISSUER_HASH, 
    	ISSUER_COMMON_NAME,
    	ISSUER_ORGANIZATION,
    	ISSUER_ORGANIZATION_UNIT,
    	ISSUER_EMAIL,
    	ISSUER_STREET,
    	ISSUER_POSTAL_CODE,
    	ISSUER_COUNTRY,
    	ISSUER_STATE_OR_PROVICE,
    	ISSUER_LOCALITY,
    	ISSUER_ID,
    	ISSUER_UNSTRUCTURED_NAME,
	};
    
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
    public static String retrievePfxProperty(File pfx, String contraseña, CERTIFICATE_PROPERTY propiedad) {
    	
    	assertEnviroment();
    	
    	//Preparando comando
    	Boolean issuerProp	=	propiedad.name().startsWith("ISSUER_") && !propiedad.name().endsWith("HASH");
    	Boolean subjectProp	=	propiedad.name().startsWith("SUBJECT_") && !propiedad.name().endsWith("HASH");

    	String prop = propiedad.name().toLowerCase();
    	
    	if(subjectProp) {
    		prop = "subject";
    	}
    	
    	if(issuerProp) {
    		prop = "issuer";
    	}
    	
    	comando 	=  	"openssl pkcs12 -in \"{pfx}\" -passin pass:\"{contraseña}\" | openssl x509 -noout -{prop}";
    	comando 	=	comando.replace("{pfx}", pfx.getPath()).replace("{contraseña}", contraseña);
    	comando		=	comando.replace("{prop}", prop);
    	
    	//Si son campos del sujeto o emisor, la respuesta tiene valores mixtos así que se debe tratar la cadena
    	String response 	= 	callCommand(comando, true);

    	if(issuerProp || subjectProp) {
        	response 	= 	response.replace("\\C3\\9A", "Á")
							        	.replace("\\C3\\89", "É")
							        	.replace("\\C3\\8D", "Í")
							        	.replace("\\C3\\93", "Ó")
							        	.replace("\\C3\\9A", "Ú")
							        	.replace("\\C3\\A1", "á")
							        	.replace("\\C3\\A9", "é")
							        	.replace("\\C3\\AD", "í")
							        	.replace("\\C3\\B3", "ó")
							        	.replace("\\C3\\BA", "ú")
        								.replace("subject=", "")
        								.replace("issuer=", "");
        				        		
        	//Separar respuesta por comas, respetando respuestas que tengan comas dentro de comillas
    		String[] partes = response.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    		
    		String identificador = "";
    		
    		switch (propiedad) {
				case SUBJECT_COMMON_NAME:
				case ISSUER_COMMON_NAME:
					identificador = "CN";
					break;
				case SUBJECT_NAME:
					identificador = "N";
					break;
				case SUBJECT_ORGANIZATION:
				case ISSUER_ORGANIZATION:
					identificador = "O";
					break;
				case ISSUER_ORGANIZATION_UNIT:
					identificador = "OU";
					break;
				case SUBJECT_COUNTRY:
				case ISSUER_COUNTRY:
					identificador = "C";
					break;
				case SUBJECT_EMAIL:
				case ISSUER_EMAIL:
					identificador = "emailAddress";
					break;
				case SUBJECT_ID:
				case ISSUER_ID:
					identificador = "x500UniqueIdentifier";
					break;
				case SUBJECT_SERIAL_NUMBER:
					identificador = "serialNumber";
					break;
				case ISSUER_STREET:
					identificador = "street";
					break;
				case ISSUER_POSTAL_CODE:
					identificador = "postalCode";
					break;
				case ISSUER_STATE_OR_PROVICE:
					identificador = "ST";
					break;
				case ISSUER_LOCALITY:
					identificador = "L";
					break;
				case ISSUER_UNSTRUCTURED_NAME:
					identificador = "unstructuredName";
				default:
			}
    		
    		for (String parte : partes) {
				if(parte.contains(identificador + " = ")) {
					response = parte.substring(identificador.length() + 3, parte.length());
					response = response.trim();
					
					if(response.startsWith("\"") && response.endsWith("\"")) {
						response = response.substring(1, response.length() - 1);
					}
					
					continue;
				}
			}
    	}
    	else {
    		switch (propiedad) {
	    		case ALIAS:
	    			if(response.equals("<No Alias>"));
	    			response = retrievePfxProperty(pfx, contraseña, CERTIFICATE_PROPERTY.SUBJECT_COMMON_NAME);
	    			break;
	    		case FINGERPRINT:
	    			response = response.replace("SHA1 Fingerprint=", "");
	    			break;
	    		case PURPOSE:
	    			BufferedReader bufReader = new BufferedReader(new StringReader(response));
	    			
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
	    			
	    			response = aux.substring(0, aux.length() - 2);
	    			break;
	    		case SERIAL:
	    			response = response.replace("serial=", "");
	    			break;
	    		case STARTDATE:
	    			response = response.replace("notBefore=", "");
	    			break;
	    		case ENDDATE:
	    			response = response.replace("notAfter=", "");
	    		default:
    		}
    	}
    	
    	return response;
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
