package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DeleteGame {

	private static Connection connection;

	public DeleteGame() {

	}

	public static void Delete(String gameName) throws Exception {
		connection = DatabaseConnection.newConnection(); // Connect to database
		Statement stmt = connection.createStatement(); // Creates a Statement object for sending SQL statements to the database

		try {
			/* Delete the pieces from the game */
			String sql = " DELETE FROM piece WHERE game_name = '" + gameName + "'";
			stmt.executeUpdate(sql);

			/* Detele the game */
			sql = " DELETE FROM save_game WHERE name = '" + gameName + "'";
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				if (stmt != null) {
					connection.close(); // Close database connection
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			try {
				if (connection != null) {
					connection.close(); // Close database connection
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
}
