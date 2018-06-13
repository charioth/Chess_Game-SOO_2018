package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import models.game.ColorInfo;
import models.game.Coordinates;
import models.pieces.Piece;
import models.pieces.PieceInfo;
import models.pieces.PieceList;

public class LoadGame {

	private static MongoDatabase db;
	private static MongoCollection<Document> collection;

	public static ColorInfo loadGame(String gameName, PieceList pieceBox[]) throws Exception {
		int i;
		
		try {
			if(db == null) {
				db = DatabaseConnection.newConnection(); // Connect to database
			}
			collection = db.getCollection("games");		
			Document searchQuery = new Document("saveName", gameName);
			Document game = collection.find(searchQuery).first();
			
			i = game.getInteger("turn");
			
			@SuppressWarnings("unchecked")
			List<Document> piecesDoc = (List<Document>) game.get("pieces");
			
			for(Document piece : piecesDoc) {
				int coord_row = piece.getInteger("coord_row");
				int coord_column = piece.getInteger("coord_column");
				int piece_type = piece.getInteger("piece_type");
				boolean moved = piece.getBoolean("moved");
				int piece_color = piece.getInteger("piece_color");
				int index = piece.getInteger("index");
				pieceBox[piece_color].getPieces()[index] = new Piece(new Coordinates(coord_row, coord_column),
						PieceInfo.values()[piece_type], moved, ColorInfo.values()[piece_color]);
				pieceBox[piece_color].getPieces()[index].setIndex(index);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ColorInfo.values()[i];
	}

	public static ArrayList<String> loadNames() throws Exception {

		ArrayList<String> games = new ArrayList<String>();
		try {
			if(db == null) {
				db = DatabaseConnection.newConnection(); // Connect to database
			}
			collection = db.getCollection("games");
			FindIterable<Document> gameSaves = collection.find();
			for(Document save : gameSaves) {
				System.out.println(save);
				String saveName = save.getString("saveName");
				games.add(saveName);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return games; // Return the list
	}
}
