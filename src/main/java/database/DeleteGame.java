package database;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DeleteGame {

	private static MongoDatabase db;
	private static MongoCollection<Document> collection;
	
	public static void Delete(String gameName) throws Exception {
		try {
			if(db == null) {
				db = DatabaseConnection.newConnection(); // Connect to database
			}
			collection = db.getCollection("games");		
			Document searchQuery = new Document("saveName", gameName);
			Document game = collection.find(searchQuery).first();
			System.out.println(game);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
