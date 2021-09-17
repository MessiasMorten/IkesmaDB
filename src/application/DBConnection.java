package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/*
 * This class was designed to make it easier to change the url for the local SQLite database
 * Rather than hardcoding the same url over and over again, it can be changed right here
 */
public class DBConnection {

	Connection c;
	String db_url = "jdbc:sqlite:C:\\Users\\Morten\\SQLite\\db\\IkesmaDB.db";
	
	public Connection createConnection() {
		
		c = null;
		
		try {
			c = DriverManager.getConnection(db_url);
		} catch (SQLException e) {
			System.out.println("Error in connection!");
		}

		return c;
		
	}
	
}
