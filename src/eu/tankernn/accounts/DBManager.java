package eu.tankernn.accounts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBManager {
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://tankernn.eu/EMP";

	// Database credentials
	static final String USER = "AccountManager";
	static final String PASS = "laUqy\\%]aeOe";

	static Connection conn = null;
	static Statement stmt = null;

	public static void init() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
	}
	
	
}
