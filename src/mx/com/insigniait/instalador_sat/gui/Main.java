package mx.com.insigniait.instalador_sat.gui;

import java.io.File;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.com.insigniait.instalador_sat.util.OpenSsl;
import mx.com.insigniait.instalador_sat.util.OpenSsl.CERTIFICATE_PROPERTY;
import mx.com.insigniait.instalador_sat.util.Util;

public class Main extends Application {

	File certificate 	= 	null;
	File key			=	null;
	
	public static void main(String[] args) {
		Application.launch(args);
	}
	
    @Override
    public void start(Stage stage) throws Exception {
        
    	//Directorio inicial
        File initialDir = new File(System.getProperty("user.home"), "documents\\Trabajo\\OpenKM\\Firma Digital Avanzada\\Instrumentos\\FIEL_MEGI720318266_20180216083404");
        
        if(!initialDir.exists()) {
        	initialDir = null;
        }
        
        //Etiquetas
        Label labelCrt 		= 	new Label("Certificado (.cer)");
        Label labelKey 		= 	new Label("Llave privada (.key)");
        Label labelPass 	= 	new Label("Contrase�a");
        
        //Campos de rutas
        TextField txtCrt = new TextField();
        txtCrt.setEditable(false);
        txtCrt.setFocusTraversable(false);
        
        TextField txtKey = new TextField();
        txtKey.setFocusTraversable(false);
        txtKey.setEditable(false);
        
        //Campo de contrase�a
        PasswordField txtPass = new PasswordField();
        
        //Campo oculto de contrase�a en texto
        TextField txtPassHidden = new TextField();
        txtPassHidden.setVisible(false);
        
        //Boton para ocultar/mostrar contrase�a
        ToggleButton mostrarContrase�a = new ToggleButton("Mostrar");
        mostrarContrase�a.setSelected(false);
        
        //Selectores de archivo
        FileChooser fileCrt = new FileChooser();
        fileCrt.setInitialDirectory(initialDir);
        fileCrt.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Certificados de seguridad", "*.cer"));
        /*Util.selectFirstElement(fileCrt);*/
        
        FileChooser fileKey = new FileChooser();
        fileKey.setInitialDirectory(initialDir);
        fileKey.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Llaves privadas", "*.key"));
        /*Util.selectFirstElement(fileKey);*/

        //Botones de selector de archivo
        Button btnCrt = new Button("Seleccionar...");
        btnCrt.setFocusTraversable(true);
        
        Button btnKey = new Button("Seleccionar...");
        btnKey.setFocusTraversable(true);
        
        //Icono de bot�n de formulario
        ImageView checkIcon = new ImageView(new Image("check.png"));
        checkIcon.setFitHeight(27);
        checkIcon.setFitWidth(27);
        
        //Bot�n de formulario
        Button btnInstalar = new Button("Instalar");
        btnInstalar.setPrefWidth(300);
        btnInstalar.setPrefHeight(70);
        btnInstalar.setGraphic(checkIcon);

        //Panel
        GridPane gridPane = new GridPane();
        gridPane.add(labelCrt, 0, 0);
        gridPane.add(labelKey, 0, 1);
        gridPane.add(labelPass, 0, 2);
        gridPane.add(btnCrt, 4, 0);
        gridPane.add(btnKey, 4, 1);
        gridPane.add(txtCrt, 1, 0, 3, 1);
        gridPane.add(txtKey, 1, 1, 3, 1);
        gridPane.add(txtPass, 1, 2, 3, 1);
        gridPane.add(mostrarContrase�a, 4, 2);
        gridPane.add(txtPassHidden, 1, 2, 3, 1);
        gridPane.add(btnInstalar, 1, 3, 3, 1);
        gridPane.setPadding(new Insets(20, 20, 20, 20));
        gridPane.setHgap(20);
        gridPane.setVgap(10);
        
        //Eventos
        
        /*
         * Eventos de enter se toman como clicks
         */
        Util.setEnterAsClick(btnCrt);
        Util.setEnterAsClick(btnKey);
        Util.setEnterAsClick(btnInstalar);
        Util.setEnterAsClick(mostrarContrase�a);
        
        /*
         * Eventos de mostrar/ocultar contrase�a
         */
        txtPassHidden.textProperty().addListener((obs, oldText, newText) -> {
        	txtPass.setText(newText);
        });
        
        mostrarContrase�a.setOnAction(e -> {
        	if(mostrarContrase�a.isSelected()) {
        		String password = txtPass.getText();

        		txtPassHidden.setVisible(true);
        		txtPassHidden.setText(password);
        		txtPass.setVisible(false);
        	}
        	else {
        		String password = txtPassHidden.getText();

        		txtPassHidden.setVisible(false);
        		txtPass.setText(password);
        		txtPass.setVisible(true);
        	}
        });
        
        /*
         * Eventos de dialogo de explorador
         */
        btnCrt.setOnAction(e -> {
        	File aux = fileCrt.showOpenDialog(stage);
        	
        	if(aux != null) {
        		certificate = aux;
        		txtCrt.setText(certificate.getPath());
        	}
        });
        
        btnKey.setOnAction(e -> {
        	File aux = fileKey.showOpenDialog(stage);

        	if(aux != null) {
        		key = aux;
        		txtKey.setText(key.getPath());
        	}
        });

        /*
         * Evento principal
         */
        btnInstalar.setOnAction(e -> {
        	if(Util.isNotBlank(txtPass.getText()) && certificate != null && key != null) {
        		
        		try {
        			String	pass		=	txtPass.getText();
        			
        			File 	pemKey 		= 	OpenSsl.keyToPem(key, pass);
        			File 	pemCert 	= 	OpenSsl.crtToPem(certificate);
        			File 	pfxCert 	=	OpenSsl.pemsToPfx(pemCert, pemKey, pass);
        			
        			OpenSsl.installCertificate(pfxCert, pass);
        			
        			String mensaje = "";
        			
        			mensaje += "===Datos del certificado==\n";
        			mensaje += "Alias: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ALIAS) + "\n";
        			mensaje += "Fecha inicial: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.STARTDATE) + "\n";
        			mensaje += "Fecha final: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ENDDATE) + "\n";
        			mensaje += "Huella digital: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.FINGERPRINT) + "\n";
        			mensaje += "Prop�sito(s): " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.PURPOSE) + "\n\n";
        			mensaje += "===Datos del emisor==\n";
        			mensaje += "Nombre com�n: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ISSUER_COMMON_NAME) + "\n";
        			mensaje += "Domicilio: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ISSUER_LOCALITY) + ", ";
        			mensaje += "calle " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ISSUER_STREET) + " ";
        			mensaje += "CP " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ISSUER_POSTAL_CODE) + ",";
					mensaje += OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ISSUER_STATE_OR_PROVICE) + ", ";
					mensaje += OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ISSUER_COUNTRY) + "\n";
        			mensaje += "Correo electr�nico: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ISSUER_EMAIL) + "\n";
        			mensaje += "Identificador: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ISSUER_ID) + "\n";
        			mensaje += "Organizaci�n: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ISSUER_ORGANIZATION) + "\n";
        			mensaje += "Unidad de organizaci�n: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ISSUER_ORGANIZATION_UNIT) + "\n";
        			mensaje += OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ISSUER_UNSTRUCTURED_NAME) + "\n\n";
        			mensaje += "===Datos del sujeto==\n";
        			mensaje += "Nombre: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.SUBJECT_NAME) + "\n";
        			mensaje += "Correo electr�nico: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.SUBJECT_EMAIL) + "\n";
        			mensaje += "Organizaci�n: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.SUBJECT_ORGANIZATION) + "\n";
        			mensaje += "Identificador: " + OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.SUBJECT_ID) + "\n";
        			
//        	        KeyStore ks = KeyStore.getInstance("Windows-MY");
//        	        ks.load(null, null); 
//        	        
//        	        ks.aliases();
//        	        ks.getCertificate(OpenSsl.retrievePfxProperty(pfxCert, pass, CERTIFICATE_PROPERTY.ALIAS));
        			
        			//Borrar archivos temporales
        			pemCert.delete();
        			pemKey.delete();
        			pfxCert.delete();
        			
        			//Mensaje final
	        		Alert alert = new Alert (AlertType.INFORMATION, mensaje, ButtonType.CLOSE); 
	        		alert.setHeaderText("Instalaci�n exitosa");
	        		alert.setTitle("Instalaci�n exitosa");
	        		alert.getDialogPane().setPrefWidth(550);
	        		alert.setResizable(false);
	        		alert.show();
        		}
        		catch(IllegalStateException ise) {
        			Alert alert = new Alert(AlertType.ERROR, ise.getMessage()); 
        			alert.show();
        		}
        	}
        	else {
        		Alert alert = new Alert(AlertType.ERROR, "Por favor, rellene los datos.\nTodos los campos son requeridos."); 
        		alert.show();
        	}
        });
        
        //Contenedor principal
        Scene scene = new Scene(gridPane, 600, 200);
        
        //Configuraci�n de ventana
        stage.setTitle("Instalador de certificados SAT");
        stage.getIcons().add(new Image("icono.png"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}