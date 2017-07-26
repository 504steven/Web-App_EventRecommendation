package rpc; // remote procedure call

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
import external.ExternalAPI;
import external.ExternalAPIFactory;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/Search")
public class SearchItem extends HttpServlet {
	//? private static final long serialVersionUID = 1L;
	private DBConnection conn = DBConnectionFactory.getDBConnection();

	/**
	 * Default constructor.
	 */
	public SearchItem() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at: ").append(request.getContextPath());

		// Test1, http://localhost:8080/Titan/Search?username=abcd
		// response.setContentType("application/json");
		// response.addHeader("Access-Control-Allow-Origin", "*");
		// PrintWriter out = response.getWriter();
		// if (request.getParameter("username") != null) {
		// String username = request.getParameter("username");
		// out.print("Hello " + username);
		// }
		// out.flush();
		// out.close();

		// Test2, http://localhost:8080/Titan/Search?username=abcd
		// response.setContentType("text/html");
		// PrintWriter out = response.getWriter();
		// out.println("<html><body>");
		// out.println("<h1>This is a HTML page</h1>");
		// out.println("</body></html>");
		// out.flush();
		// out.close();

		// Test3, http://localhost:8080/Titan/Search?username=abcd
		// response.setContentType("application/json");
		// response.addHeader("Access-Control-Allow-Origin", "*");
		// String username = "";
		// if (request.getParameter("username") != null) {
		// username = request.getParameter("username");
		// }
		// JSONObject obj = new JSONObject();
		// try {
		// obj.put("username", username);
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// PrintWriter out = response.getWriter();
		// out.print(obj);
		// out.flush();
		// out.close();

		// Test4, http://localhost:8080/Titan/Search
		// JSONArray array = new JSONArray();
		// try {
		// array.put(new JSONObject().put("username", "1234"));
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		// RpcHelper.writeJsonArray(response, array);
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String userid = request.getParameter("user_id");
		String term = request.getParameter("term");
		//ExternalAPI externalApi = ExternalAPIFactory.getExternalAPI();
		List<Item> itemlist = conn.search(userid, lat, lon, term);
		List<JSONObject> itemjsonlist = new ArrayList<>();
		for(Item item : itemlist) {
			itemjsonlist.add(item.toJSONObject());
		}
		JSONArray itemjsonarr = new JSONArray(itemjsonlist);
		RpcHelper.writeJsonArray(response, itemjsonarr);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
