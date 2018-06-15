package database;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import models.pieces.Piece;
import models.pieces.PieceList;

public class SaveGame {

	private static MongoDatabase db;
	private static MongoCollection<Document> collection;
	
	/**
	 * Save the game in the database
	 * 
	 * @param actualTurn	The turn of the active player
	 * @param pieceBox		All the game pieces
	 */
	public static void save(int actualTurn, PieceList pieceBox[]) throws Exception {
		try {
			if(db == null) {
				db = DatabaseConnection.newConnection(); // Connect to database
			}
			collection = db.getCollection("games");
			
			Date date = new Date();
			String gameName = "SaveGame" + String.format("%tH:%tM:%tS", date, date, date); // Time formatted for the 24-hour clock as "%tH:%tM:%tS"
			Document saveGave = gameToDoc(gameName, actualTurn, date, pieceBox);
			collection.insertOne(saveGave);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	private static Document gameToDoc(String gameName, int actualTurn, Date saveDate, PieceList pieceBox[]) {
		List<Document> piecesDoc = new ArrayList<Document>();

		for (PieceList pieces : pieceBox) {
			Piece [] piecesArray = pieces.getPieces();
			for(Piece p : piecesArray) {
				piecesDoc.add(p.pieceToDoc());
			}
		}

		return new Document("saveName", gameName)
				.append("date", saveDate)
				.append("turn", actualTurn)
				.append("pieces", piecesDoc);
	}
}
