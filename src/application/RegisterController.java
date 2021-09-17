package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.ResourceBundle;

import entities.Customer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RegisterController implements Initializable {

	public RegisterController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}
	
	//Connect GUI elements
	@FXML
	private TextField input_email;
	
	@FXML
	private TextField input_firstname;
	
	@FXML
	private TextField input_familyname;
	
	@FXML
	private CheckBox checker_membership;
	
	@FXML
	private TextField pw_password;
	
	@FXML
	private TextField pw_confirm;
	
	@FXML
	private Text error;
	
	@FXML
	private Text error_message;
	
	@FXML
	private Button performRegister;
	
	@FXML
	private AnchorPane anchor;
	
	
	//Method to perform registration of a new user
	public void performRegister() {
		
		//Unfortunately, SQLite does not allow for more complex user authentication or security.
		//Better security is possible to implement
		
		String password = pw_password.getText();
		String confirm = pw_confirm.getText();
		
		//Display error if user does not match password with confirm in GUI
		if (!password.equals(confirm)) {

			error.setVisible(true);
			error_message.setText("Password needs to match confirm password");
			error_message.setVisible(true);
			
		} else {
			
			//If the user is successful in matching passwords after a failed attempt,
			//the displayed error message should disappear
			error.setVisible(false);
			error_message.setVisible(false);
			
			//Note to self; For future reference, I would implement SecureRandom to generate a password hash, 
			//but the program is limited to the simple types allowed to store in a SQLite DB
		int membership = 0;
		int discount_furniture = 0; 
		int discount_textile = 0; 
		int discount_hotdog = 0;
		
		if (checker_membership.isSelected() == true) {
			membership = 1;
			
			//Generate random discounts
			Random r = new Random();
			discount_furniture = r.nextInt(10);
			discount_textile = r.nextInt(20);
			discount_hotdog = r.nextInt(35);
			
		} else {
			membership = 0;
		}
		
				
		Customer user = new Customer(
				0,							//ID not important in this context
				input_email.getText(),
				pw_confirm.getText(),
				input_firstname.getText(),
				input_familyname.getText(),
				membership,
				discount_furniture,
				discount_textile,
				discount_hotdog
				);
		
		
		//Creating sql connection to local SQLite db
		DBConnection con = new DBConnection();
		Connection c = con.createConnection();
		
		//Prepared statement to create user
		
		try { 
			PreparedStatement stmt = c.prepareStatement("INSERT INTO users(email, password, first_name, family_name, membership, discount_furniture, discount_textile, discount_hotdog) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			stmt.setString(1, user.getEmail());
			stmt.setString(2, user.getPassword());
			stmt.setString(3, user.getFirst_name());
			stmt.setString(4, user.getFamily_name());
			stmt.setInt(5, user.getMembership());
			stmt.setInt(6, user.getDiscount_furniture());
			stmt.setInt(7,  user.getDiscount_textile());
			stmt.setInt(8, user.getDiscount_hotdog());
			
			try {
				
				/*
				 * I used stmt.execute() for insert and update queries initially, but discovered that 
				 * execute() only returns true if it fetches a valid ResultSet, which is not the case for these queries
				 * New method executeUpdate() returns the effect of the query. If result > 0, at least one row has been affected
				 */
				
				int result = stmt.executeUpdate();
				
				if (result < 1) {
					System.out.println("User creation did not succeed. Email is probably in use");
					error.setVisible(true);
					error_message.setText("Email is already in use.");
					error_message.setVisible(true);
					
				} else if (result >= 1) {
					
					error.setVisible(false);
					error_message.setVisible(false);
					
					//Build main stage of shop enterprise
					System.out.println("User has been created. Redirect to shop");
					
					Stage stage = null;
					Parent root = null;
					Scene scene = null;
						
						try {
							
						//Getting current state from button
						stage = (Stage)performRegister.getScene().getWindow();
							
						root = FXMLLoader.load(getClass().getResource("Shop.fxml"));
						scene = new Scene(root,400,600);
						stage.setUserData(user);
						stage.setScene(scene);
						
						
						} catch (IOException e) {
							System.out.println("IO exception thrown at stage change");
						}
				}
				
				
			} catch (SQLException e) {
				System.out.println("Error: User creation statement did not successfully execute!");
			}  finally {
				
				try {
					c.close();
				} catch (SQLException e) {
					System.out.println("Failed to close db connection");
				}
				
			}
			
			
			
		} catch (SQLException e) {
			System.out.println("Error in statement preparation!");
		} finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				System.out.println("Failed to close db connection");
			}
			
		}
		
	}
}
}
