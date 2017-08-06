package algo;

import java.util.List;

import entity.Item;

public interface Recommendation {
	public List<Item> recomendItems(String userId, double lat, double lon);
}
