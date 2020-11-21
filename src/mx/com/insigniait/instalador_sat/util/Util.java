package mx.com.insigniait.instalador_sat.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.ButtonBase;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Util {
	
	public static boolean isNotBlank(String cad) {
		return cad == null || cad.trim().length() != 0;
	}
	
	public static String getExtension(String path) {
		if(path == null) {
			return "";
		}
		else {
			String[] parts = path.split("\\.");
			return parts[parts.length - 1];
		}
	}
	
	public static String getRealName(String path) {
		
		if(path == null) {
			return "";
		}
		else {
			String[] 	parts 	=	path.split("\\\\");
			String 		name 	= 	parts[parts.length - 1];
			
			String[] 	parts2 	= 	name.split("\\.");
			return parts2[0];
		}
	}
	
	public static <T> String join(List<T> list, String joiner) {
		String str = "";
		
		if(joiner == null) {
			joiner = " ";
		}
		
		for (T elem : list) {
			str += elem.toString() + joiner;
		}
		
		return str;
	}
	
	public static String replaceUTF8HexChars(String cad) {
		return cad.replace("\\C3\\9A", "Á")
				    	.replace("\\C3\\89", "É")
				    	.replace("\\C3\\8D", "Í")
				    	.replace("\\C3\\93", "Ó")
				    	.replace("\\C3\\9A", "Ú")
				    	.replace("\\C3\\A1", "á")
				    	.replace("\\C3\\A9", "é")
				    	.replace("\\C3\\AD", "í")
				    	.replace("\\C3\\B3", "ó")
				    	.replace("\\C3\\BA", "ú");
	}
	
	public static Map<String, String> createMapFromCommaSeparatedString(String list) {
		
    	Map<String, String>	mapa = new HashMap<String, String>();
		
		String[] partes = list.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
		
		for (String parte : partes) {
			Integer breakPoint = parte.indexOf(" = ");
			
			if(breakPoint != - 1) {
				String identificador 	= 	parte.substring(0, breakPoint);
				String valor 			= 	parte.substring(identificador.length() + 3, parte.length());
				
				identificador = identificador.trim();
				valor = valor.trim();
				
				if(valor.startsWith("\"") && valor.endsWith("\"")) {
					valor = valor.substring(1, valor.length() - 1);
				}
				
				mapa.put(identificador, valor);
			}
		}
		
		return mapa;
	}
	
	public static void setEnterAsClick(ButtonBase btn) {
		btn.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
            	btn.fire();
            }
        });
	}
	
	public static void selectFirstElement(FileChooser fileChooser) {
		
		File initialDir = fileChooser.getInitialDirectory();
		
		if(initialDir != null) {
			
			List<ExtensionFilter> filtrosExt = fileChooser.getExtensionFilters();
			
			if(filtrosExt != null) {
				
				FilenameFilter filtroNombre = new FilenameFilter() { 
					public boolean accept(File f, String name) 
					{ 
						Boolean aceptado = false;
						
						for (ExtensionFilter filtroExt : filtrosExt) {
							for (String ext : filtroExt.getExtensions()) {
								ext = ext.replaceAll("\\*", "");
								aceptado = aceptado || name.endsWith(ext);
							}
						}
						
						return aceptado; 
					} 
				}; 
				
				File[] filteredFiles = initialDir.listFiles(filtroNombre);
				
				if(filteredFiles.length > 0) {
					fileChooser.setInitialFileName(filteredFiles[0].getName());
				}
			}
			
		}
	}
}
