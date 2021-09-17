package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;

import entities.Customer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MembershipController implements Initializable {

	
	Customer currentUser;
	
	public MembershipController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
	
	//Connecting GUI elements to controller
	@FXML
	private Text text_firstname;
	@FXML
	private Text text_familyname;
	@FXML
	private Text text_membership;
	@FXML
	private Button member_btn;
	@FXML
	private Button fetchUserInfo;
	@FXML
	private Text success;
	@FXML
	private Text success_text;
	
	/*
	 * Refreshes information on the membership stage. 
	 * Has to be called to display information as I could not figure out how to fetch
	 * data from a stage during initialization.
	 * The initialize function is unstable as the stage is still building, 
	 * and can throw runtime errors
	 */
	public void updateUserInfo() {
		
		//Gets user data passed from shop stage
		Stage currentStage = (Stage)fetchUserInfo.getScene().getWindow();
		currentUser = (Customer) currentStage.getUserData();
		
		//Updates information about user
		text_firstname.setText(currentUser.getFirst_name());
		text_familyname.setText(currentUser.getFamily_name());
		
		//Logic to display current information on the button
		if (currentUser.getMembership() <= 0) {
			member_btn.setText("Become a member");
			member_btn.setVisible(true);
			text_membership.setText("FALSE");
			Paint p = Color.RED;
			text_membership.setFill(p);
		} else {
			member_btn.setText("Leave membership");
			member_btn.setVisible(true);
			text_membership.setText("TRUE");
			Paint p = Color.GREEN;
			text_membership.setFill(p);
		}
	}

	
	//Logic to either make the user a member or remove a users membership benefits
	public void updateMembership() {
		if (currentUser.getMembership() <= 0) {
			
			//Make user a member
			int membership = 1;
			int discount_furniture = 0; 
			int discount_textile = 0; 
			int discount_hotdog = 0;
			
			//Generate random discounts
			Random r = new Random();
			discount_furniture = r.nextInt(10);
			discount_textile = r.nextInt(20);
			discount_hotdog = r.nextInt(35);
			
			//Creating sql connection to local SQLite db
			DBConnection con = new DBConnection();
			Connection c = con.createConnection();
			
			//Prepared statement to update user
			try { 
				PreparedStatement stmt = c.prepareStatement("UPDATE users SET membership=?, discount_furniture=?, discount_textile=?, discount_hotdog=? WHERE id=?");
				stmt.setInt(1, membership);
				stmt.setInt(2, discount_furniture);
				stmt.setInt(3,  discount_textile);
				stmt.setInt(4, discount_hotdog);
				stmt.setInt(5,  currentUser.getUserid());
				
				int result = stmt.executeUpdate();
				
				Paint colorSuccess = Color.GREEN;
				Paint colorFailed = Color.RED;
			
				if (result >= 1) {
					
					//Update current user
					currentUser.setMembership(1);
					currentUser.setDiscount_furniture(discount_furniture);
					currentUser.setDiscount_textile(discount_textile);
					currentUser.setDiscount_hotdog(discount_hotdog);
					
					member_btn.setText("Leave membership");
					text_membership.setText("TRUE");
					text_membership.setFill(colorSuccess);
					success.setFill(colorSuccess);
					success_text.setFill(colorSuccess);
					success.setText("Success:");
					success_text.setText("You are now a subscribed member!");
					success.setVisible(true);
					success_text.setVisible(true);
					
				} else if (result < 1) {
					
					success.setFill(colorFailed);
					success_text.setFill(colorFailed);
					success.setText("Failed:");
					success_text.setText("Subscription update failed.");
					success.setVisible(true);
					success.setVisible(true);
					
				}
				
			} catch (SQLException e) {
				System.out.println("Query to update user status to member failed.");
			}  finally {
				
				try {
					c.close();
				} catch (SQLException e) {
					System.out.println("Failed to close db connection");
				}
				
			}
			
			
		} else {
			//Make user lose member status and discounts
			//Creating sql connection to local SQLite db
			DBConnection con = new DBConnection();
			Connection c = con.createConnection();
			
			//Prepared statement to update user
			try { 
				PreparedStatement stmt = c.prepareStatement("UPDATE users SET membership=?, discount_furniture=?, discount_textile=?, discount_hotdog=? WHERE id=?");
				stmt.setInt(1, 0);
				stmt.setInt(2, 0);
				stmt.setInt(3, 0);
				stmt.setInt(4, 0);
				stmt.setInt(5,  currentUser.getUserid());
				
				int result = stmt.executeUpdate();
				
				Paint colorSuccess = Color.GREEN;
				Paint colorFailed = Color.RED;
			
				if (result >= 1) {
					
					//Update current user
					currentUser.setMembership(0);
					currentUser.setDiscount_furniture(0);
					currentUser.setDiscount_textile(0);
					currentUser.setDiscount_hotdog(0);
					
					text_membership.setText("FALSE");
					text_membership.setFill(colorFailed);
					success.setFill(colorSuccess);
					success_text.setFill(colorSuccess);
					success.setText("Success:");
					success_text.setText("You are not a subscribed member any more!");
					success.setVisible(true);
					success_text.setVisible(true);
					
				} else if (result < 1) {
					success.setFill(colorFailed);
					success_text.setFill(colorFailed);
					success.setText("Failed:");
					success_text.setText("Subscription update failed.");
					success.setVisible(true);
					success_text.setVisible(true);
				}
				
			} catch (SQLException e) {
				System.out.println("Query to update user status to member failed.");
			}  finally {
				
				try {
					c.close();
				} catch (SQLException e) {
					System.out.println("Failed to close db connection");
				}
				
			}
		}
	}

	public void moveToShop() {

		Stage currentStage = (Stage)member_btn.getScene().getWindow();
		currentUser = (Customer) currentStage.getUserData();
		
		Stage stage = null;
		Parent root = null;
		Scene scene = null;
			
			try {
				
			//Getting current state from button
			stage = (Stage)member_btn.getScene().getWindow();
				
			root = FXMLLoader.load(getClass().getResource("Shop.fxml"));
			scene = new Scene(root,400,600);
			stage.setUserData(currentUser);
			stage.setScene(scene);
			
			
			} catch (IOException e) {
				System.out.println("IO exception thrown at stage change");
			}
	}
	
}
