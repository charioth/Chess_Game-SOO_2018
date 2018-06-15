package database;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnection {
	private static MongoClient client;
	private static String databaseName = "chessGame";
	
	/**
	  * Create a new connection with a MongoDB database
	  * 
	  * @return a new connection with a MongoDB database
	  */
	public synchronized static MongoDatabase newConnection() throws Exception {
		if(client != null) {
			return client.getDatabase(databaseName);
		}
		
		try {
			client = new MongoClient("localhost", 27017);
			return client.getDatabase(databaseName);
		} catch (Exception e) {
			System.out.println("Problem connecting with database");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
