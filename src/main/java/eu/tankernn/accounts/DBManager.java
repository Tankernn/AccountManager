package eu.tankernn.accounts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBManager {
	// JDBC driver name and database URL
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost/AccountManager";

	// Database credentials
	private static final String USER = "root";
	private static final String PASS = "password";

	private static Connection conn = null;
	private static Statement stmt = null;

	public static void init() {
		if (inited()) // Already inited
			return;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			// Check if table exists and create it otherwise
			stmt = conn.createStatement();
			conn.createStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS Accounts (FirstName varchar(255), LastName varchar(255), AccountNumber varchar(30) NOT NULL UNIQUE, PRIMARY KEY (AccountNumber))");
			conn.createStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS Events (uid int AUTO_INCREMENT UNIQUE, account varchar(255), balanceChange double, description varchar(512), PRIMARY KEY (uid))");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
	}

	public static void saveAccounts(List<Account> accounts) {
		try {
			conn.createStatement().executeUpdate("DELETE FROM Events");
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		for (Account a : accounts) {
			try {
				PreparedStatement ps = conn.prepareStatement(
						"INSERT IGNORE INTO Accounts (firstName, lastName, accountNumber) VALUES (?, ?, ?)");
				ps.setString(1, a.firstName);
				ps.setString(2, a.lastName);
				ps.setString(3, a.accountNumber);
				saveAccountEvents(a.accountNumber, a.history);
				ps.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static List<AccountEvent> getAccountEvents(String accountNumber) {
		try {
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Events WHERE account=?");
			ps.setString(1, accountNumber);
			ResultSet set = ps.executeQuery();
			List<AccountEvent> events = new ArrayList<AccountEvent>();
			while (set.next()) {
				events.add(new AccountEvent(set.getDouble("balanceChange"), set.getString("description")));
			}
			return events;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void saveAccountEvents(String accountNumber, List<AccountEvent> events) {
		for (AccountEvent e : events)
			try {
				PreparedStatement ps = conn.prepareStatement(
						"INSERT INTO Events (account, balanceChange, description) VALUES (?, ?, ?)");
				ps.setString(1, accountNumber);
				ps.setDouble(2, e.balanceChange);
				ps.setString(3, e.description);
				ps.executeUpdate();
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
	}

	private static Account accountFromResultSet(ResultSet set) throws SQLException {
		String num = set.getString("accountNumber");
		return new Account(set.getString("firstName"), set.getString("lastName"), num, getAccountEvents(num));
	}

	public static List<Account> readAccounts() {
		List<Account> accounts = new ArrayList<Account>();
		try {
			stmt = conn.createStatement();
			stmt.executeQuery("SELECT * FROM Accounts");
			ResultSet set = stmt.getResultSet();
			while (set.next()) {
				accounts.add(accountFromResultSet(set));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return accounts;
	}

	public static boolean inited() {
		return conn != null;
	}
}
