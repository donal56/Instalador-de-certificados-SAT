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
import mx.com.insigniait.instalador_sat.dto.Pfx;
import mx.com.insigniait.instalador_sat.util.OpenSsl;
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
        File initialDir = new File(System.getProperty("user.home"), "documents");
        
        if(!initialDir.exists()) {
        	initialDir = null;
        }
        
        //Etiquetas
        Label labelCrt 		= 	new Label("Certificado (.cer)");
        Label labelKey 		= 	new Label("Llave privada (.key)");
        Label labelPass 	= 	new Label("Contraseña");
        
        //Campos de rutas
        TextField txtCrt = new TextField();
        txtCrt.setEditable(false);
        txtCrt.setFocusTraversable(false);
        
        TextField txtKey = new TextField();
        txtKey.setFocusTraversable(false);
        txtKey.setEditable(false);
        
        //Campo de contraseña
        PasswordField txtPass = new PasswordField();
        
        //Campo oculto de contraseña en texto
        TextField txtPassHidden = new TextField();
        txtPassHidden.setVisible(false);
        
        //Boton para ocultar/mostrar contraseña
        ToggleButton mostrarContraseña = new ToggleButton("Mostrar");
        mostrarContraseña.setSelected(false);
        
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
        
        //Icono de botón de formulario
        ImageView checkIcon = new ImageView(new Image("check.png"));
        checkIcon.setFitHeight(22);
        checkIcon.setFitWidth(22);
        
        //Botón de formulario
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
        gridPane.add(mostrarContraseña, 4, 2);
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
        Util.setEnterAsClick(mostrarContraseña);
        
        /*
         * Eventos de mostrar/ocultar contraseña
         */
        txtPassHidden.textProperty().addListener((obs, oldText, newText) -> {
        	txtPass.setText(newText);
        });
        
        mostrarContraseña.setOnAction(e -> {
        	if(mostrarContraseña.isSelected()) {
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
        			
        			Pfx pfxProps = OpenSsl.retrievePfxProperty(pfxCert, pass);
        			
        			String mensaje = "";
        			
        			mensaje += "===Datos del certificado===\n";
        			mensaje += "Alias: " + pfxProps.getAlias() + "\n";
        			mensaje += "Fecha inicial: " + pfxProps.getStartDate() + "\n";
        			mensaje += "Fecha final: " + pfxProps.getEndDate() + "\n";
        			mensaje += "Huella digital: " + pfxProps.getFingerprint() + "\n";
        			mensaje += "Propósito(s): " + pfxProps.getPurposes() + "\n\n";
        			mensaje += "===Datos del emisor===\n";
        			mensaje += "Nombre común: " + pfxProps.getIssuer().getCommonName() + "\n";
        			mensaje += "Domicilio: " + pfxProps.getIssuer().getLocality() + ", ";
        			mensaje += "calle " + pfxProps.getIssuer().getStreet() + " ";
        			mensaje += "CP " + pfxProps.getIssuer().getPostalCode() + ",";
					mensaje += pfxProps.getIssuer().getState() + ", " + pfxProps.getIssuer().getCountry() + "\n";
        			mensaje += "Correo electrónico: " + pfxProps.getIssuer().getEmail() + "\n";
        			mensaje += "Identificador: " + pfxProps.getIssuer().getId() + "\n";
        			mensaje += "Organización: " + pfxProps.getIssuer().getOrganization() + "\n";
        			mensaje += "Unidad de organización: " + pfxProps.getIssuer().getOrganizationUnit() + "\n";
        			mensaje += pfxProps.getIssuer().getUnstructuredName() + "\n\n";
        			mensaje += "===Datos del sujeto===\n";
        			mensaje += "Nombre: " + pfxProps.getSubject().getName() + "\n";
        			mensaje += "Correo electrónico: " + pfxProps.getSubject().getEmail() + "\n";
        			mensaje += "Organización: " + pfxProps.getSubject().getOrganization() + "\n";
        			mensaje += "Identificador: " + pfxProps.getSubject().getId() + "\n";
        			
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
	        		alert.setHeaderText("Instalación exitosa");
	        		alert.setTitle("Instalación exitosa");
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
        
        //Configuración de ventana
        stage.setTitle("Instalador de certificados SAT");
        stage.getIcons().add(new Image("icono.png"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
