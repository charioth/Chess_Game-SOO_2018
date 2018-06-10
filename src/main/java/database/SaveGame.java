package database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import pieces.PieceList;

public class SaveGame {

	private static Connection connection;

	public SaveGame() {

	}

	public static void save(int actualTurn, PieceList pieceBox[]) throws Exception {
		connection = DatabaseConnection.newConnection(); // Connect to database
		Statement stmt = connection.createStatement(); // Creates a Statement object for sending SQL statements to the database

		String gameName;
		Date date = new Date();
		try {
			// Automatically created name
			gameName = "SaveGame" + String.format("%tH:%tM:%tS ", date, date, date); // Time formatted for the 24-hour clock as "%tH:%tM:%tS"
			saveGame(gameName, actualTurn, String.format("%tm/%td/%ty", date, date, date), stmt); // Date formatted as "%tm/%td/%ty"
			savePieces(gameName, pieceBox, stmt); // Save the pieces information

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				if (stmt != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	public static void saveGame(String gameName, int actualTurn, String saveDate, Statement stmt) throws Exception {
		/* Save the game information in the database */
		try {
			String sql = "INSERT INTO save_game(name, save_date, turn) VALUES ('" + gameName + "'" + ", " + "'"
					+ saveDate + "'" + ", " + actualTurn + ");";
			stmt.executeUpdate(sql);

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void savePieces(String gameName, PieceList pieceBox[], Statement stmt) throws Exception {

		try {

			int coord_row;
			int coord_column;
			int piece_type;
			boolean moved;
			int piece_color;
			int index;

			for (PieceList pieces : pieceBox) { // Each PieceList have all the pieces of a color
				for (int i = 0; i < 16; i++) { // 16 total pieces per color in chess
					coord_row = pieces.getPieces()[i].getActualPosition().getRow();
					coord_column = pieces.getPieces()[i].getActualPosition().getColumn();
					piece_type = pieces.getPieces()[i].getType().value;
					moved = pieces.getPieces()[i].isMoved();
					piece_color = pieces.getPieces()[i].getColor().value;
					index = pieces.getPieces()[i].getIndex();

					String sql = "INSERT INTO piece (game_name, coord_row, coord_column, piece_type, moved, piece_color, piece_index)"
							+ " VALUES ('" + gameName + "'" + ", " + coord_row + ", " + coord_column + ", " + piece_type
							+ ", " + moved + ", " + piece_color + ", " + index + ");";
					stmt.executeUpdate(sql); // Save each piece in the database
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
