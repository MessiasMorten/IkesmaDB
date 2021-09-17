package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class IkesmaController implements Initializable {

	public IkesmaController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		// TODO Auto-generated method stub

	}
	
	
	//Connect GUI elements to controller
	@FXML
	private Button login_button;
	
	@FXML
	private Button register_button;
	
	
	//Method called by the GUI to move to Register stage
	public void moveToRegister() {
		
		Stage stage = null;
		Parent root = null;
		Scene scene = null;
		
		try {
			
			
		//Getting current state from button
		stage = (Stage)register_button.getScene().getWindow();
	
		root = FXMLLoader.load(getClass().getResource("Register.fxml"));
		scene = new Scene(root,400,600);
		stage.setScene(scene);
		
		
		} catch (IOException e) {
			System.out.println("IO exception thrown at stage change");
		}
	}

	//Method called by the GUI to move to Login stage
	public void moveToLogin() {
		
		Stage stage = null;
		Parent root = null;
		Scene scene = null;
		
		try {
			
		//Getting current state from button
		stage = (Stage)login_button.getScene().getWindow();
			
		root = FXMLLoader.load(getClass().getResource("Login.fxml"));
		scene = new Scene(root,400,600);
		stage.setScene(scene);
		
		
		} catch (IOException e) {
			System.out.println("IO exception thrown at stage change");
			e.printStackTrace();
		}
	}
}
