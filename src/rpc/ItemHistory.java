package rpc;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/History")
public class ItemHistory extends HttpServlet {
	// private static final long serialVersionUID = 1L; // ??
	private DBConnection conn = DBConnectionFactory.getDBConnection();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ItemHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		Set<Item> eventSet = conn.getFavoriteItems(userId);
	    JSONArray array = new JSONArray();
	    for (Item item : eventSet) {
	    	JSONObject obj = item.toJSONObject();
	    	System.out.println("gethistory:" + obj);
	    	try {
	    		obj.put("favorite", true);
	    	} catch (JSONException e) {
	    		e.printStackTrace();
	    	}
	    	array.put(obj);
	   }
	   
	   RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			if (input.has("user_id") && input.has("favorite")) {
				String userId = input.getString("user_id");
				JSONArray array = input.getJSONArray("favorite");
				List<String> favoriteEventIdList = new ArrayList<>();
				for (int i = 0; i < array.length(); i++) {
					String eventId = (String) array.get(i);
					favoriteEventIdList.add(eventId);
				}
				conn.setFavoriteItems(userId, favoriteEventIdList);
				// TODO: logic to process visitedEvents
				RpcHelper.writeJsonObject(response, new JSONObject().put("status", "success"));
			} else {
				RpcHelper.writeJsonObject(response, new JSONObject().put("status", "InvalidParameter"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*
	 * @see HttpServlet#doDelete(HttpServletRequest request, HttpServletResponse
	 * response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
		try {	 
			JSONObject input = RpcHelper.readJsonObject(request);
			if(input.has("user_id") && !input.isNull("favorite") ) {
				String userId = input.getString("user_id");
				JSONArray evnetIdList = input.getJSONArray("favorite");
				List<String> favoriteEvnetIdList = new ArrayList<>();
				for(int j = 0; j < evnetIdList.length(); j++ ) {
					favoriteEvnetIdList.add(evnetIdList.getString(j));
				}
				conn.unsetFavoriteItems(userId, favoriteEvnetIdList);
				RpcHelper.writeJsonObject(response, new JSONObject().put("status", "success"));
		    }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
