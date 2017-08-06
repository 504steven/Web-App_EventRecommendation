package rpc;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

import algo.GeoRecommendation;
import algo.Recommendation;
import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class RecommendItem
 */
@WebServlet("/Recommendation")
public class RecommendItem extends HttpServlet {
	// private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getParameter("user_id");
		double lon = Double.parseDouble( request.getParameter("lon") );
		double lat = Double.parseDouble( request.getParameter("lat") );
		DBConnection conn = DBConnectionFactory.getDBConnection();
		Recommendation recommendation = new GeoRecommendation();
		List<Item> eventList = recommendation.recomendItems(userId, lat, lon);
		JSONArray array = new JSONArray();
		for(Item item : eventList) {
			array.put(item.toJSONObject());
		}
	    RpcHelper.writeJsonArray(response, array);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
