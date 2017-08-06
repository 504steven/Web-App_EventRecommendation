
package db.mongodb;

import java.util.*;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import db.DBConnection;
import db.mongodb.MongoDBUtil;
import entity.Item;
import external.ExternalAPI;
import external.ExternalAPIFactory;

public class MongoDBConnection implements DBConnection {
	private static MongoDBConnection instance;
	private MongoClient mongoClient;
	private MongoDatabase db;
	
	public static DBConnection getInstance() {
		if (instance == null) {
			instance = new MongoDBConnection();
		}
		return instance;
	}

	private MongoDBConnection() {
		// Connects to local mongodb server.
		mongoClient = new MongoClient();
		db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);
	}

	@Override
	public void close() {
		if (mongoClient != null) {
			mongoClient.close();
		}
	}

	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		String sql = "INSERT INTO history (user_id, item_id) VALUES(?,?)";
		//PreparedStatement stmt = conn.pre
		
	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<String> getFavoriteItemIds(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Item> getFavoriteItems(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getCategories(String itemId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveItem(Item item) {
		UpdateOptions options = new UpdateOptions().upsert(true);
		db.getCollection("items").updateOne(new Document().append("item_id", item.getItemId()),
				new Document("$set",
						new Document().append("item_id", item.getItemId()).append("name", item.getName())
								.append("city", item.getCity()).append("state", item.getState())
								.append("country", item.getCountry()).append("zip_code", item.getZipcode())
								.append("rating", item.getRating()).append("address", item.getAddress())
								.append("latitude", item.getLatitude()).append("longitude", item.getLongitude())
								.append("description", item.getDescription()).append("snippet", item.getSnippet())
								.append("snippet_url", item.getSnippetUrl()).append("image_url", item.getImageUrl())
								.append("url", item.getUrl()).append("categories", item.getCategories())),
				options);
	}

	@Override
	public List<Item> searchItems(String userId, double lat, double lon, String term) {
		// Connect to external API
		ExternalAPI api = ExternalAPIFactory.getExternalAPI();
		List<Item> items = api.search(lat, lon, term);
		for (Item item : items) {
			// Save the item into our own db.
			saveItem(item);
		}
		return items;
	}

}
