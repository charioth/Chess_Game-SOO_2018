package database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import models.pieces.Piece;
import models.pieces.PieceList;

public class SaveGame {

	private static MongoDatabase db;
	private static MongoCollection<Document> collection;

	/**
	 * If it`s a loaded game update the game in the database else save the game
	 * 
	 * @param actualGameName
	 *            name of the game loaded from database else it`s null
	 * @param actualTurn
	 *            The turn of the active player
	 * @param pieceBox
	 *            All the game pieces
	 */
	public static void save(String actualGameName, int actualTurn, PieceList pieceBox[]) throws Exception {
		try {
			if (db == null) {
				db = DatabaseConnection.newConnection(); // Connect to database
			}
			collection = db.getCollection("games"); // Get collection of games from database

			Date date = new Date();
			String gameName = "SaveGame" + String.format("%tH:%tM:%tS", date, date, date); // Time formatted for the
																							// 24-hour clock as
																							// "%tH:%tM:%tS"
			Document query;
			if (actualGameName == null) { // If it`s not a load game, then use a new game value to insert
				query = new Document("saveName", gameName);
			} else { // Else use the old name to only update the fields
				query = new Document("saveName", actualGameName);
			}

			Document saveGame = gameToDoc(gameName, actualTurn, date, pieceBox);
			UpdateOptions options = new UpdateOptions().upsert(true); // Upsert true, if query is true than update else
																		// insert

			collection.replaceOne(query, saveGame, options);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private static Document gameToDoc(String gameName, int actualTurn, Date saveDate, PieceList pieceBox[]) {
		List<Document> piecesDoc = new ArrayList<Document>();

		for (PieceList pieces : pieceBox) {
			Piece[] piecesArray = pieces.getPieces();
			for (Piece p : piecesArray) {
				piecesDoc.add(p.pieceToDoc());
			}
		}

		return new Document("saveName", gameName).append("date", saveDate).append("turn", actualTurn).append("pieces",
				piecesDoc);
	}
}
