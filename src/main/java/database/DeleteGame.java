package database;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DeleteGame {

	private static MongoDatabase db;
	private static MongoCollection<Document> collection;
	
	/**
	 * Delete a given game from the database
	 * 
	 * @param gameName The name of the game to be deleted
	 */
	public static void Delete(String gameName) throws Exception {
		try {
			if(db == null) {
				db = DatabaseConnection.newConnection(); // Connect to database
			}
			collection = db.getCollection("games");		
			Document searchQuery = new Document("saveName", gameName);
			collection.deleteOne(searchQuery);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
