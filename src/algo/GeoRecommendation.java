package algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation implements Recommendation {

	@Override
	public List<Item> recomendItems(String userId, double lat, double lon) {
		DBConnection conn = DBConnectionFactory.getDBConnection();
		Set<String> favItemIdSet = conn.getFavoriteItemIds(userId);
		Set<String> favCategorySet = new HashSet<>();
		for(String id : favItemIdSet) {
			favCategorySet.addAll( conn.getCategories(id) );
		}
		Set<Item> recommendedItems = new HashSet<>();
		for(String category : favCategorySet) {
			recommendedItems.addAll( conn.searchItems(userId, lat, lon, category) );
		}
		
		Iterator it = recommendedItems.iterator();
		while(it.hasNext()) {
			if( favItemIdSet.contains( ((Item)it.next()).getItemId() ) ) {
				it.remove();
			}
		}
		
		List<Item> filteredItem = new ArrayList<Item>( recommendedItems );
		// step 5. perform ranking of these items based on distance.
	    Collections.sort(filteredItem, new Comparator<Item>() {
	      @Override
	      public int compare(Item item1, Item item2) {
	        // Student question: can we make this ranking even better with more dimensions?
	        // What other feathers can be used here?
	        double distance1 = GeoRecommendation.getDistance(item1.getLatitude(), item1.getLongitude(), lat, lon);
	        double distance2 = GeoRecommendation.getDistance(item2.getLatitude(), item2.getLongitude(), lat, lon);
	        // return the increasing order of distance.
	        return Double.compare(distance1, distance2);
	      }
	    });
		return filteredItem;
	 }

	 // Calculate the distances between two geolocations.
	 // Source : http://andrew.hedges.name/experiments/haversine/
	 private static double getDistance(double lat1, double lon1, double lat2, double lon2) {
	    double dlon = lon2 - lon1;
	    double dlat = lat2 - lat1;
	    double a = Math.sin(dlat / 2 / 180 * Math.PI) * Math.sin(dlat / 2 / 180 * Math.PI)
	        + Math.cos(lat1 / 180 * Math.PI) * Math.cos(lat2 / 180 * Math.PI)
	            * Math.sin(dlon / 2 / 180 * Math.PI) * Math.sin(dlon / 2 / 180 * Math.PI);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
	    // Radius of earth in miles.
	    double R = 3961;
	    return R * c;
	 }
	
}
