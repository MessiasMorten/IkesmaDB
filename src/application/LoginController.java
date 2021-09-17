package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import entities.Customer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController implements Initializable {

	public LoginController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

	}
	
	
	//Connecting GUI elements to controller
	@FXML
	private TextField input_email;
	
	@FXML
	private TextField input_password;
	
	@FXML
	private Button button_login;
	
	//Function called by GUI login button to perform login and move user to the shop interface
	public void perform_login() {
		
		//Creating sql connection to local SQLite db
		DBConnection con = new DBConnection();
		Connection c = con.createConnection();
				
				//Prepared statement to fetch user
				try { 
					PreparedStatement stmt = c.prepareStatement("SELECT * FROM users WHERE email=? LIMIT 1");
					stmt.setString(1, input_email.getText());
					
					ResultSet result = stmt.executeQuery();
					int numCols = result.getMetaData().getColumnCount();
					
					if (numCols <= 0) {
						System.out.println("Failed to query the database for user.");
					
					}  else {
						
						String email="", password="", firstname="", familyname="";
						int id=0, membership=0, discount_furniture=0, discount_textile=0, discount_hotdog=0;
								
								while (result.next()) {
									id = result.getInt(1);
									email = result.getString(2);
									password = result.getString(3);
									firstname = result.getString(4);
									familyname = result.getString(5);
									membership = result.getInt(6);
									discount_furniture = result.getInt(7);
									discount_textile = result.getInt(8);
									discount_hotdog = result.getInt(9);
								}
								result.close();
								Customer user = new Customer(
										id, email, password, firstname, familyname, 
										membership, discount_furniture, discount_textile, discount_hotdog);

						
						if (!input_password.getText().equals(user.getPassword())) {
							//TODO: Implement graphics for error messages
							System.out.println("Wrong password, pirate.");
						} else {
							
							Stage stage = null;
							Parent root = null;
							Scene scene = null;
								
								try {
									
								//Getting current state from button
								stage = (Stage)button_login.getScene().getWindow();
									
								root = FXMLLoader.load(getClass().getResource("Shop.fxml"));
								scene = new Scene(root,400,600);
								stage.setUserData(user);
								stage.setScene(scene);
							
								
								} catch (IOException e) {
									System.out.println("IO exception thrown at stage change");
								}
							
						}
					}
					
				} catch (SQLException e) {
					System.out.println("Failed to query database for user");
				} finally {
					
					try {
						c.close();
					} catch (SQLException e) {
						System.out.println("Failed to close db connection");
					}
					
				}
	}

}
