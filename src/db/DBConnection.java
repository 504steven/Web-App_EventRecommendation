package db;

import java.util.List;
import java.util.Set;

import entity.Item;

public interface DBConnection {
	/**
	 * 
	 */
	public void close();
	
	public void setFavoriteItems(String userid, List<String> itemidlist);
	
	public void unsetFavoriteItems(String userid, List<String> itemidlist);
	
	public Set<String> getFavoriteItemIDs(String userid);
	
	public Set<Item> getFavoriteItems(String userid);
	
	public Set<String> getCategories(String itemid);
	
	public List<Item> search(String userid, double lat, double lon, String term);
	
	/**
	 * 
	 * @param item
	 */
	public void saveItem(Item item);
}
