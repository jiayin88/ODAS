package userProfile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

//To create a database
public class Database {

	/** The name of the MySQL account to use (or empty for anonymous) */
	public final static String userName = "root";

	/** The password for the MySQL account (or empty for anonymous) */
	public final static String password = "odas";

	/** The name of the computer running MySQL */
	public final static String serverName = "localhost";

	/** The port of the MySQL server (default is 3306) */
	public final static int portNumber = 3306;

	/** The name of the database we are testing with (this default is installed with MySQL) */
	public final static String dbName = "odas";
	

	
	/**
	 * Get a new database connection
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", Database.userName);
		connectionProps.put("password", Database.password);

		conn = DriverManager.getConnection("jdbc:mysql://"
				+ Database.serverName + ":" + Database.portNumber + "/" + Database.dbName,
				connectionProps);

		return conn;
	}

	/**
	 * Run a SQL command which does not return a recordset:
	 * CREATE/INSERT/UPDATE/DELETE/DROP/etc.
	 * 
	 * @throws SQLException If something goes wrong
	 */
	public boolean executeUpdate(Connection conn, String command) throws SQLException {
	    Statement stmt = null;
	    try {
	        stmt = conn.createStatement();
	        stmt.executeUpdate(command); // This will throw a SQLException if it fails
	        return true;
	    } finally {

	    	// This will run whether we throw an exception or not
	        if (stmt != null) { stmt.close(); }
	    }
	}
	
	/**
	 * Connect to MySQL and do some stuff.
	 */
	public void run() {

		
	}
	
	/**
	 * Connect to the DB and do some stuff
	 */
	public static void main(String[] args) {
		Database app = new Database();
		app.run();
	}
}
