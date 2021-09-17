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
import entities.Furniture;
import entities.Hotdog;
import entities.Order;
import entities.OrderLine;
import entities.Product;
import entities.Textile;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class StoreController implements Initializable {
	
	//CurrentOrder is being used to reference the order the user is creating at the current time
	Customer currentUser;
	Order currentOrder;
	
	//Connecting GUI elements to connector
	@FXML
	private Accordion storeContainer;
	
	@FXML
	private Button get_store_contents;
	
	@FXML
	private Accordion orderContainer;
	
	@FXML
	private Button btn_goback;
	
	@FXML
	private Button btn_submitorder;
	
	@FXML
	private Text text_currentorder;
	
	
	public StoreController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	//This is the main method that fetches all data into the store interface
	public void getStoreContents() {
		
		//Update the user info parsed from the previous stage
		Stage currentStage = (Stage)get_store_contents.getScene().getWindow();
		currentUser = (Customer) currentStage.getUserData();
		
		//Create db connection
		DBConnection con = new DBConnection();
		Connection c = con.createConnection();
		
		//Prepared statement to retrieve store contents
		try { 
			PreparedStatement stmt = c.prepareStatement("SELECT * FROM products ORDER BY id ASC LIMIT ?");
			stmt.setInt(1, 100);
			ResultSet res = stmt.executeQuery();
			
			/*  This area presented an issue for me, and something I could not solve fully myself.
			 *  ResultSet can only fetch one column at a time, and you can't directly fetch rows.
			 *  Given how the database has been structured, and how the objects are instances of 
			 *  their respective categories, I got some support off the internet
			 *  Full disclosure; I did not personally write the method to implement row fetching as hashmaps from a resultset
			 *  
			 *  Link here: https://stackoverflow.com/questions/11824258/how-to-fetch-entire-row-as-array-of-objects-with-jdbc/11826814
			 */
			
			//Parsing column names into an arraylist for reference
			ResultSetMetaData meta = stmt.getMetaData();
			int numCols = meta.getColumnCount();	
		    ArrayList<String> cols = new ArrayList<String>();
		    for (int i=1; i<=numCols; i++) {
		    	cols.add(meta.getColumnName(i));
		    }
		   
		    //Creating an arraylist of hashmaps that reference columns and rows in the resultset
		    ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();

		    while (res.next()) {
		    	HashMap<String,Object> row = new HashMap<String,Object>();
		    		for (String colName:cols) {
		    	        Object val = res.getObject(colName);
		    	        row.put(colName,val);
		    	      }
		    	rows.add(row);
		    }
		    
		    //Array of titled panes that will be added to the existing Accordion
		    TitledPane[] panes = new TitledPane[rows.size()];
		    
		    
		    //Iterates through the data and creates visuals for the GUI
		    for (int i=0; i<rows.size(); i++) {
		    	
		    	HashMap<String, Object> row = rows.get(i);
		    	
		    	String desc = (String) row.get("description");
		    	int price = (int) row.get("price");
		    	int in_stock = (int) row.get("in_stock");
		    	
		    	int itemnumber;
		    	try {
		    	itemnumber = (int) row.get("itemNo");
		    	} catch (Exception e){
		    		itemnumber = 0;
		    	}
		    	
		    	
		    	GridPane grid = new GridPane();
		    	Text description = new Text(desc);
		    	if (itemnumber >= 1) {
		    		
		    		Text itemNo = new Text("itemNo: " + itemnumber + "   ");
			    	GridPane.setRowIndex(itemNo, 0);
			    	GridPane.setColumnIndex(itemNo, 0);
			    	grid.getChildren().add(itemNo);
			    	
			    	Text cost = new Text("Cost: " + price + " NOK" + "   ");
			    	GridPane.setRowIndex(cost, 0);
			    	GridPane.setColumnIndex(cost, 1);
			    	grid.getChildren().add(cost);
			    	
			    	Text stock = new Text("In stock: " + in_stock + "   ");
			    	GridPane.setRowIndex(stock, 0);
			    	GridPane.setColumnIndex(stock, 2);
			    	grid.getChildren().add(stock);
			    	
			    	if (in_stock > 0) {
			    		
			        Button buyItem = new Button("Buy");
			        Paint p = Color.DARKGREEN;
			    	buyItem.setTextFill(p);
			    	

			    	//Sets an actionevent for when the buy button is clicked, and what happens when it does
			    	buyItem.setOnAction(e -> {
			    		
			    	      int id = (int) row.get("id");
			    	      int itemnum = 0;
			    	      itemnum = (int) row.get("itemNo");
			    	      int product_cost = (int) row.get("price");
			    	      String product_desc = (String) row.get("description");
			    	      
			    	      int userid = currentUser.getUserid();
			    	      currentOrder = getOrCreateOrder(userid);
			    	      
			    	      OrderLine line = new OrderLine();
			    	      line.setOrder_id(currentOrder.getId());
			    	      line.setDb_id(id);
			    	      line.setProduct_id(itemnum);
			    	      line.setCost(product_cost);
			    	      line.setDescription(product_desc);
			    	      
			    	      //This method is called to create an orderline in db from the parsed orderline object
			    	      createOrderLineInDatabase(line);
			    	      
			    	      //The reason calling a function to fetch the orderlines from the db, is to get their database reference id into the order object
			    	      currentOrder.setOrderlines(getOrderLinesForOrder(currentOrder.getId()));
			    	      
			    	      //This function is called to update GUI for the store
			    	      setGUIforOrder();
			    	
			    	});
			    	
			    	GridPane.setRowIndex(buyItem, 0);
			    	GridPane.setColumnIndex(buyItem, 3);
			    	grid.getChildren().add(buyItem);
			    	
			    		
			    	} else {
			    		
			    		//If a product is out of stock, graphics needs to display that
			    		Text out_of_stock = new Text("Out of stock");
			    		Paint p = Color.DARKRED;
			    		out_of_stock.setFill(p);
				    	GridPane.setRowIndex(out_of_stock, 0);
				    	GridPane.setColumnIndex(out_of_stock, 3);
				    	grid.getChildren().add(out_of_stock);
			    		
			    	}
			    	
			    	
		    	} else {
		    		
		    		//Setting up graphics for products without an itemNo
		    		Text cost = new Text("Cost: " + price + " NOK" + "   ");
			    	GridPane.setRowIndex(cost, 0);
			    	GridPane.setColumnIndex(cost, 1);
			    	grid.getChildren().add(cost);
			    	
			    	Text stock = new Text("In stock: " + in_stock + "   ");
			    	GridPane.setRowIndex(stock, 0);
			    	GridPane.setColumnIndex(stock, 2);
			    	grid.getChildren().add(stock);
			    	
			    	if (in_stock > 0) {
			    		
			        Button buyItem = new Button("Buy");
			        Paint p = Color.DARKGREEN;
			    	buyItem.setTextFill(p);
			    	GridPane.setRowIndex(buyItem, 0);
			    	GridPane.setColumnIndex(buyItem, 3);
			    
			    	//Action event is largely the same as for products with itemNo's
			    	buyItem.setOnAction(e -> {

			    	      int id = (int) row.get("id");
			    	      int itemnum = 0;
			    	      int product_cost = (int) row.get("price");
			    	      String product_desc = (String) row.get("description");
			    	      
			    	      int userid = currentUser.getUserid();

			    	      
			    	      currentOrder = getOrCreateOrder(userid);
			    	      
			    	      OrderLine line = new OrderLine();
			    	      line.setOrder_id(currentOrder.getId());
			    	      line.setDb_id(id);
			    	      line.setProduct_id(itemnum);
			    	      line.setCost(product_cost);
			    	      line.setDescription(product_desc);
			    	      createOrderLineInDatabase(line);
			    	      
			    	      currentOrder.setOrderlines(getOrderLinesForOrder(currentOrder.getId()));
			    	      setGUIforOrder();
			    	
			    	});
			    	grid.getChildren().add(buyItem);
			    		
			    	} else {
			    		
			    		Text out_of_stock = new Text("Out of stock");
			    		Paint p = Color.DARKRED;
			    		out_of_stock.setFill(p);
				    	GridPane.setRowIndex(out_of_stock, 0);
				    	GridPane.setColumnIndex(out_of_stock, 3);
				    	grid.getChildren().add(out_of_stock);
			    		
			    	}
		    		
		    	}
		    	
		    	//Set grid properties, create a titledpane with the grid
		    	Insets in = new Insets(15.0, 15.0, 15.0, 15.0);
		    	grid.setPadding(in);
		    	grid.setMinWidth(500);
		    	TitledPane pane = new TitledPane(desc, grid);
		    	panes[i] = pane;
		    	pane.setText(desc);
		    	
		    }
		    
		    //Inserts all panes into the storeContainer(Accordion)
		    storeContainer.getPanes().addAll(panes);
			
		} catch (SQLException e) {
			System.out.println("Query to update user status to member failed.");
		} finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				System.out.println("Failed to close db connection");
			}
			
		}
	}

	//This method is called each time a product is added to checkout, but returns the active order if one has already been created
	private Order getOrCreateOrder(int userid) {
		
		if (currentOrder == null) {
			
			//Create db connection
			DBConnection con = new DBConnection();
			Connection c = con.createConnection();
			
			//Prepared statement to retrieve store contents
			try { 
				PreparedStatement stmt = c.prepareStatement("INSERT INTO orders(userid, shipped) VALUES (?, ?)");
				stmt.setInt(1, userid);
				stmt.setInt(2, 0); //Orders created should be tagged as not shipped by default
				int result = stmt.executeUpdate();
				
				if (result < 1) {
					System.out.println("Create order statement failed");
				} else {
					
					text_currentorder.setText("Current Order");
					Paint p = Color.BLACK;
					text_currentorder.setFill(p);
					
					System.out.println("Order created, check db reference");
	
				}
				
			} catch (SQLException e) {
				System.out.println("Create order statement failed.");
			} finally {
				
				try {
					c.close();
				} catch (SQLException e) {
					System.out.println("Failed to close db connection");
				}
				
			}
			
		} else {
			//Enables the method to be ran even if an Order object has been created
			return currentOrder;
		}
		
		//Fetches the new order from db, to get proper id
		//Also adds an orderline object
		Order order = new Order();
		
		//Creates db connection
		DBConnection con = new DBConnection();
		Connection c = con.createConnection();
		
		//Prepared statement to select the most recent created order
		try { 
			PreparedStatement stmt = c.prepareStatement("SELECT * FROM orders WHERE userid=? ORDER BY id desc LIMIT 1");
			stmt.setInt(1, userid);
			ResultSet result = stmt.executeQuery();
			
				while(result.next()) {
					order.setId(result.getInt(1));
					order.setUserid(result.getInt(2));
					order.setShipped(result.getInt(3));
					order.setOrderlines(null);
				}
				
				//Again, the method fetches orderlines directly from the db
				order.setOrderlines(getOrderLinesForOrder(order.getId()));
				
			
		} catch (SQLException e) {
			System.out.println("Select order statement failed");
		} finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				System.out.println("Failed to close db connection");
			}
			
		}
		
		return order;
	}

	//Largely a GUI function, to make sure info is displayed properly
	private void setGUIforOrder() {
		
		//Gets current order, orderlines and creates a gridpane in preparation to GUI update
		GridPane grid = new GridPane();
		currentOrder.setOrderlines(getOrderLinesForOrder(currentOrder.getId()));
		OrderLine[] lines = currentOrder.getOrderlines();
		
		//Iterates through the information to create graphics for the GUI
		for(int i=0; i<lines.length; i++) {
			
			OrderLine line = lines[i];
			Text itemNo;
			
			if (line.getProduct_id() < 1) {
				
				itemNo = new Text(line.getDescription());
				
			} else {
	
				itemNo = new Text("itemNo: " + line.getProduct_id());
			
			}
			
			//Basic, boilerplate GUI creation
			GridPane.setRowIndex(itemNo, i);
			GridPane.setColumnIndex(itemNo, 0);
			grid.getChildren().add(itemNo);

			Text cost = new Text("  Cost: " + line.getCost() + " NOK  ");
			GridPane.setRowIndex(cost, i);
			GridPane.setColumnIndex(cost, 1);
			grid.getChildren().add(cost);
			
			if (line.getDiscount() > 0) {
				Text discount = new Text("  Discount: -" + line.getDiscount() + " NOK  ");
				Paint p = Color.DARKGREEN;
				discount.setFill(p);
				GridPane.setRowIndex(discount, i);
				GridPane.setColumnIndex(discount, 2);
				grid.getChildren().add(discount);
			}
			
			Button cancelLine = new Button("Cancel");
			Paint p = Color.DARKRED;
			cancelLine.setTextFill(p);
			//Action event to delete orderlines from db and from order graphics
			cancelLine.setOnAction(e -> {
				
	    	      int id = line.getId();
	    	      deleteOrderline(id);
	    	      
	    	});
			GridPane.setRowIndex(cancelLine, i);
			GridPane.setColumnIndex(cancelLine, 3);
			grid.getChildren().add(cancelLine);
			
		}
		
		//Update the current GUI information
		TitledPane pane = new TitledPane("OrderNo: " + currentOrder.getId(), grid);
		orderContainer.getPanes().clear();
		orderContainer.getPanes().add(pane);
		orderContainer.setExpandedPane(pane);
		
	}
	
	
	//Method to create an orderline in the database
	private void createOrderLineInDatabase(OrderLine line) {
		
		/*
		 * Calls a function that creates products as their subcategory based on
		 * their 'db_id', which is represented by the primary key of the products table
		 */
		Product product = getProductById((line.getDb_id()));
		
		if (product.getDiscount() > 0) {
			line.setCost(product.getPrice());
			line.setDiscount(product.getDiscount());
		}
		
		//Creates db connection
		DBConnection con = new DBConnection();
		Connection c = con.createConnection();
		
		//Prepared statement to retrieve store contents
		try { 
			PreparedStatement stmt = c.prepareStatement("INSERT INTO orderline(order_id, db_id, product_id, cost, description, discount) VALUES (?, ?, ?, ?, ?, ?)");
			stmt.setInt(1, line.getOrder_id());
			stmt.setInt(2, line.getDb_id());
			stmt.setInt(3, line.getProduct_id());
			stmt.setInt(4, line.getCost());
			stmt.setString(5, line.getDescription());
			stmt.setInt(6, line.getDiscount());
			
			int result = stmt.executeUpdate();
			
			if (result > 0) {
				System.out.println("Order line created in db");
				
			} else {
				//TODO: Better error handling 
				System.out.println("Order line failed to save into db");
			}
		
			
		} catch (SQLException e) {
			System.out.println("Error writing orderline to database");
		} finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				System.out.println("Failed to close db connection");
			}
			
		}
		
		
	}

	//Method to get orderlines from an order based on their parsed order_id
	private OrderLine[] getOrderLinesForOrder(int id) {
		
		//Creates db connection
		DBConnection con = new DBConnection();
		Connection c = con.createConnection();
		
		//Prepared statement to select orderlines for an order in the db
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
		    
		    //Create an arraylist of hashmaps that is structured around the columns and rows of the db
		    ArrayList<HashMap<String,Object>> rows = new ArrayList<HashMap<String,Object>>();

		    while (res.next()) {
		    	HashMap<String,Object> row = new HashMap<String,Object>();
		    		for (String colName:cols) {
		    	        Object val = res.getObject(colName);
		    	        row.put(colName,val);
		    	      }
		    	rows.add(row);
		    }
		    
		    int numRows = rows.size();
			OrderLine[] lines = new OrderLine[numRows];
		    
			//Iterates through result and creates an orderline array based on db info parsed
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
		} finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				System.out.println("Failed to close db connection");
			}
			
		}
		
		//Unreachable if properly fetched
		return new OrderLine[0];
	}

	//Method created to aid certain other functions by getting products by id
	private Product getProductById(int id) {
		
		Product product = null;

		//Creates db connection
		DBConnection con = new DBConnection();
		Connection c = con.createConnection();
		
		//Prepared statement to retrieve store contents
		try { 
			
			PreparedStatement stmt = c.prepareStatement("SELECT * FROM products WHERE id=? LIMIT 1");
			stmt.setInt(1, id);
			
			ResultSet result = stmt.executeQuery();
			int product_id=0, itemNo=0, price=0, weight=0, in_stock=0;
			String description=null, flavor=null, color=null, product_flavor=null;
			
			while(result.next()) {
				product_id = result.getInt(1);
				itemNo = result.getInt(2);
				price = result.getInt(3);
				description = result.getString(4);
				weight = result.getInt(5);
				color = result.getString(6);
				flavor = result.getString(7);
				in_stock = result.getInt(8);
			}
			
			//Product is hotdog
			if (weight == 0 && color == null) {
				product = new Hotdog(price, description, flavor);
			} 
			//Product is furniture
			else if (color == null && flavor == null) {
				product = new Furniture(price, description, itemNo, weight);
			}
			//Product is textile
			else if (weight == 0 && flavor == null) {
				product = new Textile(price, description, itemNo, color);
			}
			
			/*
			 * An elaborate if-else if maze to decide product categories and possible membership discounts
			 */
			if (currentUser.getMembership() > 0 && product.getClass() == Furniture.class) {
				
				double old_price = product.getPrice();
				double percentageDiscount = (100 - currentUser.getDiscount_furniture());
				double new_price = (old_price / 100) * percentageDiscount;
				double total_discount = (old_price - new_price);
				
				int new_price_int = (int) Math.round(new_price);
				int total_discount_int = (int) Math.round(total_discount);
				
				product.setDiscount(total_discount_int);
				product.setPrice(new_price_int);
				
			} else if (currentUser.getMembership() > 0 && product.getClass() == Textile.class) {
				
				double old_price = product.getPrice();
				double percentageDiscount = (100 - currentUser.getDiscount_textile());
				double new_price = (old_price / 100) * percentageDiscount;
				double total_discount = (old_price - new_price);
				
				int new_price_int = (int) Math.round(new_price);
				int total_discount_int = (int) Math.round(total_discount);
				
				product.setDiscount(total_discount_int);
				product.setPrice(new_price_int);
				
				
			} else if (currentUser.getMembership() > 0 && product.getClass() == Hotdog.class) {
				
				double old_price = product.getPrice();
				double percentageDiscount = (100 - currentUser.getDiscount_hotdog());
				double new_price = (old_price / 100) * percentageDiscount;
				double total_discount = (old_price - new_price);
				
				int new_price_int = (int) Math.round(new_price);
				int total_discount_int = (int) Math.round(total_discount);
				
				product.setDiscount(total_discount_int);
				product.setPrice(new_price_int);
				
			
			}
			
			
		} catch (SQLException e) {
			System.out.println("Select product by id failed");
		} finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				System.out.println("Failed to close db connection");
			}
			
		}

		return product;
		
	}

	//Deletes orderline from database
	private void deleteOrderline(int id) {
		
		//Create db connection
		DBConnection con = new DBConnection();
		Connection c = con.createConnection();
		
		//Prepared statement to delete orderline
		try { 
			PreparedStatement stmt = c.prepareStatement("DELETE FROM orderline WHERE id=?");
			stmt.setInt(1, id);
			
			int result = stmt.executeUpdate();
	
			if (result > 0) {
				
				//Updates the orderline array for the current order
				OrderLine[] old_lines = currentOrder.getOrderlines();
				OrderLine[] new_lines = new OrderLine[old_lines.length - 1];
				
				if (old_lines.length <= 1) {
					currentOrder.setOrderlines(new OrderLine[0]);
				} else {
					
				for (int i=0; i<new_lines.length; i++) {
					if (old_lines[i].getId() != id)
					new_lines[i] = old_lines[i];
				}
				
				currentOrder.setOrderlines(new_lines);
				
				}
				setGUIforOrder();
				System.out.println("Order line deleted from db");
				
			} else {
				System.out.println("Order line failed to delete from db");
			}
			
			
			
		} catch (SQLException e) {
			System.out.println("Error deleting orderline from database");
		} finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				System.out.println("Failed to close db connection");
			}
			
		}
		
		
	}
	
	/*
	 * This function was created as a replacement for a larger vision that I did not get a chance to do due to limited time
	 * Instead of creating a client server app that both communicates with a db, its now a pure client app
	 * Meaning, it has been hardcoded to remove stock from the product line if a user submits an order
	 */
	private void submitOrderline(OrderLine line) {
		
		//Create db connection
		DBConnection con = new DBConnection();
		Connection c = con.createConnection();
		
		//Prepared statement to retrieve store contents
		try { 
			PreparedStatement stmt = c.prepareStatement("UPDATE products SET in_stock=in_stock-1 WHERE id=?");
			stmt.setInt(1, line.getDb_id());
			
			int result = stmt.executeUpdate();
	
			if (result > 0) {
				System.out.println("Orderline has been submitted");
			} else {
				System.out.println("Orderline failed to be submitted");
			}
			
			
			
		} catch (SQLException e) {
			System.out.println("Error submitting orderline to database");
		} finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				System.out.println("Failed to close db connection");
			}
			
		}
			
		
	}
	
	//Method to return to the shop menu interface
	public void goBack() {
		
		Stage stage = null;
		Parent root = null;
		Scene scene = null;
			
			try {
				
			//Getting current state from button
			stage = (Stage)btn_goback.getScene().getWindow();
				
			root = FXMLLoader.load(getClass().getResource("Shop.fxml"));
			scene = new Scene(root,400,600);
			stage.setUserData(currentUser);
			stage.setScene(scene);
			
			
			} catch (IOException e) {
				System.out.println("IO exception thrown at stage change");
			}
		
	}
	
	//Method as described above with orderlines
	public void submitOrder() {
		
		OrderLine[] lines = currentOrder.getOrderlines();
		
		for(int i=0; i<lines.length; i++) {
			
			OrderLine line = lines[i];
			submitOrderline(line);
			
		}
		
		//Create db connection
		DBConnection con = new DBConnection();
		Connection c = con.createConnection();
		
		//Prepared statement to retrieve store contents
		try { 
			PreparedStatement stmt = c.prepareStatement("UPDATE orders SET shipped=1 WHERE id=?");
			stmt.setInt(1, currentOrder.getId());
			
			int result = stmt.executeUpdate();
	
			if (result > 0) {
				
				//Order has been submitted, so now there's no active order anymore
				currentOrder = null;
				
				//Display success message for user
				text_currentorder.setText("Order has been processed and shipped.");
				Paint p = Color.DARKGREEN;
				text_currentorder.setFill(p);
				
				
				System.out.println("Order has been marked as shipped");
				
			} else {
				System.out.println("Order failed to ship");
			}
			
		} catch (SQLException e) {
			System.out.println("Error shipping order");
		} finally {
			
			try {
				c.close();
			} catch (SQLException e) {
				System.out.println("Failed to close db connection");
			}
			
		}
		
		//Clear the active order from GUI
		orderContainer.getPanes().clear();
		
	}
	
}
 