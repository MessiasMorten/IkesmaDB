package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import entities.Customer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/*ShopController is a controller for GUI in the user management and shop space
*A customer object is passed around as a data variable for the stages as a crude, simplistic auth
*/
public class ShopController implements Initializable {
	
	Customer currentUser = null;

	
	//Connecting GUI elements to controller
	@FXML
	private Text text_welcome;
	@FXML
	private AnchorPane anchor;
	@FXML
	private Button button_membership;
	@FXML
	private Button button_orderhistory;
	@FXML
	private Button button_cancelorder;
	
	
	/*
	 * Shop, albeit poorly named, is nothing more than a menu
	 */
	
	public ShopController() {
	}

	@Override
	public void initialize(URL url, ResourceBundle bundle) {
		// TODO Auto-generated method stub
		
	}
	
	//Method to change stage to membership
	public void moveToMembership() {

		Stage currentStage = (Stage)button_membership.getScene().getWindow();
		currentUser = (Customer) currentStage.getUserData();
		
		Stage stage = null;
		Parent root = null;
		Scene scene = null;
			
			try {
				
			//Getting current state from button
			stage = (Stage)button_membership.getScene().getWindow();
			
			root = FXMLLoader.load(getClass().getResource("Membership.fxml"));
			scene = new Scene(root,400,600);
			stage.setUserData(currentUser);
			stage.setScene(scene);
			
			} catch (IOException e) {
				System.out.println("IO exception thrown at stage change");
			}
	}	

	//Method to change stage to Order History
	public void moveToOrderHistory() {

		Stage currentStage = (Stage)button_membership.getScene().getWindow();
		currentUser = (Customer) currentStage.getUserData();
		
		Stage stage = null;
		Parent root = null;
		Scene scene = null;
			
			try {
				
			//Getting current state from button
			stage = (Stage)button_membership.getScene().getWindow();
				
			root = FXMLLoader.load(getClass().getResource("OrderHistory.fxml"));
			scene = new Scene(root,400,600);
			stage.setUserData(currentUser);
			stage.setScene(scene);
			
			
			} catch (IOException e) {
				System.out.println("IO exception thrown at stage change");
				e.printStackTrace();
			}
	}
	
	//Method to change stage to Store
	public void moveToStore() {

		Stage currentStage = (Stage)button_membership.getScene().getWindow();
		currentUser = (Customer) currentStage.getUserData();
		
		Stage stage = null;
		Parent root = null;
		Scene scene = null;
			
			try {
				
			//Getting current state from button
			stage = (Stage)button_membership.getScene().getWindow();
				
			root = FXMLLoader.load(getClass().getResource("Store.fxml"));
			scene = new Scene(root,400,600);
			stage.setUserData(currentUser);
			stage.setScene(scene);
			
			
			} catch (IOException e) {
				System.out.println("IO exception thrown at stage change");
				e.printStackTrace();
			}
	}



}
