package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import entities.Customer;
import entities.Order;
import entities.OrderLine;
import entities.Product;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class OrderHistoryController implements Initializable {
	
	Customer currentUser = null;
	
	//Connecting GUI elements to controller
	@FXML
	private AnchorPane anchor;

	@FXML
	private Button btn_goback;
	
	@FXML
	private Button btn_refresh;
	
	@FXML
	private Accordion orderHistory;
	
	public OrderHistoryController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}

	//Method to send user back to the Shop interface
	public void goBack() {
		
		Stage stage = null;
		Parent root = null;
		Scene scene = null;
			
			try {
				
			//Getting current state from button
			stage = (Stage)btn_goback.getScene().getWindow();
			
			//Switch stages
			currentUser = (Customer) stage.getUserData();	
			root = FXMLLoader.load(getClass().getResource("Shop.fxml"));
			scene = new Scene(root,400,600);
			stage.setUserData(currentUser);
			stage.setScene(scene);
			
			
			} catch (IOException e) {
				System.out.println("IO exception thrown at stage change");
			}
		
	}
	
	//Method to get orderlines for an order. Method has been used in this class, and in StoreController
	private OrderLine[] getOrderLinesForOrder(int id) {
		
			//Connection to database
			DBConnection con = new DBConnection();
			Connection c = con.createConnection();
		
		try { 
			PreparedStatement stmt = c.prepareStatement("SELECT * FROM orderline WHERE order_id=?");
			stmt.setInt(1, id);
			ResultSet res = stmt.executeQuery();
			
			//Parsing column names into an arraylist for reference
			ResultSetMetaData meta = stmt.getMetaData();
			int numCols = meta.getColumnCount();
			
		    ArrayList<String> cols = new ArrayList<String>();
		    for (int i=1; i<=numCols; i++) {
		    	cols.add(meta.getColumnName(i));
		    }
		    
		    //Creating a hashmap of rows in the resultset
		    ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();

		    while (res.next()) {
		    	HashMap<String,Object> row = new HashMap<String,Object>();
		    		for (String colName:cols) {
		    	        Object val = res.getObject(colName);
		    	        row.put(colName,val);
		    	      }
		    	rows.add(row);
		    }
		    
		    //Creating an array of OrderLine objects to match the number of objects fetched
		    int numRows = rows.size();
			OrderLine[] lines = new OrderLine[numRows];
		    
			//For loop to iterate through the orderlines and populate each object with the correct data
		    for(int i=0; i<rows.size(); i++) {	
		    	HashMap<String, Object> row = rows.get(i);
		    	
		    	OrderLine line = new OrderLine();
		    	
		    	line.setId((int) row.get("id"));
		    	line.setDb_id((int) row.get("db_id"));
		    	line.setOrder_id((int) row.get("order_id"));
		    	line.setProduct_id((int) row.get("product_id"));
		    	line.setCost((int) row.get("cost"));
		    	line.setDiscount((int) row.get("discount"));
		    	line.setDescription((String) row.get("description"));
		    	lines[i] = line;
		    }
		    
		    return lines;
		    
		} catch (SQLException e) {
			System.out.println("Select order statement failed");
		}  finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				System.out.println("Failed to close db connection");
			}
			
		}
		
		//Unreachable if properly fetched
		return new OrderLine[0];
	}

	//Method to fetch, organize and display order histories into an accordion
	public void populateAccordion() {
		
		//Clearing the accordion, just in case user decides to refresh more than once, lol
		orderHistory.getPanes().clear();
		
		//Get parsed user data
		Stage currentStage = (Stage)btn_refresh.getScene().getWindow();
		currentUser = (Customer) currentStage.getUserData();
		
		//Create db connection
		DBConnection con = new DBConnection();
		Connection c = con.createConnection();
		
		//Prepared statement to retrieve store contents
		try { 
			
			PreparedStatement stmt = c.prepareStatement("SELECT * FROM orders WHERE userid=? AND shipped=1");
			stmt.setInt(1, currentUser.getUserid());
			
			ResultSet res = stmt.executeQuery();

			//Parsing column names into an arraylist for reference
			ResultSetMetaData meta = stmt.getMetaData();
			int numCols = meta.getColumnCount();
			
		    ArrayList<String> cols = new ArrayList<String>();
		    for (int i=1; i<=numCols; i++) {
		    	cols.add(meta.getColumnName(i));
		    }
		    
		    //Hashmap of rows fetched
		    ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();

		    while (res.next()) {
		    	HashMap<String,Object> row = new HashMap<String,Object>();
		    		for (String colName:cols) {
		    	        Object val = res.getObject(colName);
		    	        row.put(colName,val);
		    	      }
		    	rows.add(row);
		    }
		    
		    //Creating an array of Order objects to display
		    int numRows = rows.size();
		    Order[] orders = new Order[numRows];
		    
			for (int i=0; i<rows.size(); i++) {
				
				HashMap<String, Object> row = rows.get(i);
				int order_id = (int)row.get("id");
				
				Order order = new Order();
				order.setId(order_id);
				order.setOrderlines(getOrderLinesForOrder(order_id));
				order.setShipped((int)row.get("shipped"));
				order.setUserid((int)row.get("userid"));
				
				orders[i] = order;
			}
			
			//Iterate through each order to prepare data for display
			for(int i=0; i<orders.length; i++) {
				
				Order order = orders[i];
				OrderLine[] lines = order.getOrderlines();
				GridPane grid = new GridPane();
				
				//Iterate through orderlines for each order(will run through all orderlines and be re-called for each order to run again)
				for (int j=0; j<lines.length; j++) {
					OrderLine line = lines[j];
					
					//Prepare the display resources
					Text desc = new Text(line.getDescription() + "    ");
					GridPane.setColumnIndex(desc, 0);
					GridPane.setRowIndex(desc, j);
					desc.setWrappingWidth(240);
					grid.getChildren().add(desc);
					
					Text cost = new Text(line.getCost() + " NOK    ");
					GridPane.setColumnIndex(cost, 1);
					GridPane.setRowIndex(cost, j);
					grid.getChildren().add(cost);
					
					
					System.out.println(line.getDiscount());
					Text discount = new Text(line.getDiscount() + " NOK    ");
					GridPane.setColumnIndex(discount, 2);
					GridPane.setRowIndex(discount, j);
					grid.getChildren().add(discount);
				}
				
				//Creating and add a titledpane that will house the gridpane and the information added to the gridpane through iteration
				TitledPane pane = new TitledPane("OrderNo: "+ order.getId(), grid);
				orderHistory.getPanes().add(pane);
			}
			
	} catch (SQLException e) {
			System.out.println("Failed to fetch orders from db");
		}  finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				System.out.println("Failed to close db connection");
			}
			
		}
		
		
	}
}
