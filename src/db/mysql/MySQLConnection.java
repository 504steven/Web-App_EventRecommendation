package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.ExternalAPI;
import external.ExternalAPIFactory;

public class MySQLConnection implements DBConnection {
	private static MySQLConnection instance;
	private Connection conn = null;
	
	public static DBConnection getInstance() {
		if(instance == null) {
			instance = new MySQLConnection();
		}
		return instance;
	}
	
	private MySQLConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection(MySQLDBUtil.URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setFavoriteItems(String userid, List<String> itemidlist) {
		String query = "INSERT INTO history (user_id, item_id) VALUES (?, ?)";
		try {
			PreparedStatement statement = conn.prepareStatement(query);
			for (String itemId : itemidlist) {
				statement.setString(1, userid);
				statement.setString(2, itemId);
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void unsetFavoriteItems(String userid, List<String> itemidlist) {
		String sql = "DELETE FROM history WHERE user_id =? and item_id =?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			for(String id : itemidlist) {
				stmt.setString(1, userid);
				stmt.setString(2, id);
				stmt.execute();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Set<String> getFavoriteItemIds(String userid) {
		Set<String> eventIdSet = new HashSet<>();
		String sql = "SELECT item_id from history where user_id = ?";
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, userid);
			ResultSet resultSet = stmt.executeQuery();
			while(resultSet.next()) {
				eventIdSet.add( resultSet.getString("item_id") );
				// System.out.println("favEventId:" + resultSet.getString("item_id"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return eventIdSet;
	}

	@Override
	public Set<Item> getFavoriteItems(String userid) {
		Set<String> itemIdSet = this.getFavoriteItemIds(userid);
		Set<Item> itemSet = new HashSet<>();
		ItemBuilder itemBuilder = new ItemBuilder();
		try {
			for(String id : itemIdSet) {
				String sql = "SELECT * FROM items WHERE item_id = ?";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, id);
				ResultSet rs = stmt.executeQuery();
				if( rs.next() ) {
				   // System.out.println("xxx" + id );  //rs.getString("item_id")
					itemBuilder.setItemId(id);
					itemBuilder.setName(rs.getString("name"));
					itemBuilder.setCity(rs.getString("city"));
					itemBuilder.setState(rs.getString("state"));
					itemBuilder.setCountry(rs.getString("country"));
					itemBuilder.setZipcode(rs.getString("zipcode"));
					itemBuilder.setRating(rs.getDouble("rating"));
					itemBuilder.setAddress(rs.getString("address"));
					itemBuilder.setLatitude(rs.getDouble("latitude"));
					itemBuilder.setLongitude(rs.getDouble("longitude"));
					itemBuilder.setDescription(rs.getString("description"));
					itemBuilder.setSnippet(rs.getString("snippet"));
					itemBuilder.setSnippetUrl(rs.getString("snippet_url"));
					itemBuilder.setImageUrl(rs.getString("image_url"));

					//find categories
					sql = "SELECT category From categories WHERE item_id = ?";
					stmt = conn.prepareStatement(sql);
					stmt.setString(1, id);
					rs = stmt.executeQuery();
					Set<String> itemCategorySet = new HashSet<>();
					while( rs.next() ) {
						itemCategorySet.add(rs.getString("category"));
					}
					itemBuilder.setCategories(itemCategorySet);
					Item item = itemBuilder.build();
					itemSet.add(item);
				}
				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return itemSet;
	}

	@Override
	public Set<String> getCategories(String itemid) {
		Set<String> categorySet = new HashSet<>();
		try {
			String sql = "SELECT category FROM categories WHERE item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setString(1, itemid);
			ResultSet rs = stmt.executeQuery();
			while( rs.next() ) {
				categorySet.add(rs.getString("category"));			
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return categorySet;
	}

	@Override
	public List<Item> searchItems(String userid, double lat, double lon, String term) {
		// Connect to external API
		ExternalAPI api = ExternalAPIFactory.getExternalAPI(); 
		List<Item> itemlist = api.search(lat, lon, term);
		for (Item item : itemlist) {
			// Save the item into our own db.
			saveItem(item);
		}
		return itemlist;
	}

//	@Override
//	public List<Item> searchItems(String userId, double lat, double lon, String category) {
//		List<Item> eventList = this.search(userId, lat, lon, null);
//		Iterator iterator = new Iterator();
//		
//		return null;
//	}
	
	@Override
	public void saveItem(Item item) {
		String sql = "INSERT IGNORE INTO items VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		try {
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, item.getItemId());
			statement.setString(2, item.getName());
			statement.setString(3, item.getCity());
			statement.setString(4, item.getState());
			statement.setString(5, item.getCountry());
			statement.setString(6, item.getZipcode());
			statement.setDouble(7, item.getRating());
			statement.setString(8, item.getAddress());
			statement.setDouble(9, item.getLatitude());
			statement.setDouble(10, item.getLongitude());
			statement.setString(11, item.getDescription());
			statement.setString(12, item.getSnippet());
			statement.setString(13, item.getSnippetUrl());
			statement.setString(14, item.getImageUrl());
			statement.setString(15, item.getUrl());
			statement.execute();
			
			sql = "INSERT IGNORE INTO categories VALUES (?,?)";
			for (String category : item.getCategories()) {
				statement = conn.prepareStatement(sql);
				statement.setString(1, item.getItemId());
				statement.setString(2, category);
				statement.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
