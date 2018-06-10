package database;

import java.sql.*;

public class DatabaseConnection {

	public DatabaseConnection() {

	}

	public static Connection newConnection() throws Exception {
		try {
			Class.forName("com.mysql.jdbc.Driver"); // Load JDBC driver
			return DriverManager.getConnection("jdbc:mysql://localhost:3306/TheLastChessGame", "root", "pass"); // Return connection with database
		} catch (Exception e) {
			System.out.println("Problem connecting with database");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
