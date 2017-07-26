package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void unsetFavoriteItems(String userid, List<String> itemidlist) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<String> getFavoriteItemIDs(String userid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Item> getFavoriteItems(String userid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getCategories(String itemid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> search(String userid, double lat, double lon, String term) {
		// Connect to external API
		ExternalAPI api = ExternalAPIFactory.getExternalAPI(); 
		List<Item> itemlist = api.search(lat, lon, term);
		for (Item item : itemlist) {
			// Save the item into our own db.
			saveItem(item);
		}
		return itemlist;
	}

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
