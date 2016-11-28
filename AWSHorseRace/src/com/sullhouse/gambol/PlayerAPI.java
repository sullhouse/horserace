package com.sullhouse.gambol;

import java.io.BufferedReader;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.ParseException;
import org.json.JSONObject;

public class PlayerAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public PlayerAPI() {
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email;
		if (request.getParameter("email")!=null) {
			email = request.getParameter("email");
			PlayerDatabaseAccess playerDatabaseAccess = new PlayerDatabaseAccess();
			Player player = playerDatabaseAccess.getPlayerByEmail(email);
			JSONObject playerJson = new JSONObject(player);
			playerJson.remove("code");
			response.getWriter().append(playerJson.toString());
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().append("{ }");
		}
		response.setContentType("application/json");		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) jb.append(line);
		} catch (Exception e) { 
			/*report an error*/ 
		}
		
		try {
			JSONObject jsonObject = new JSONObject(jb.toString());
			if (jsonObject.has("playerName")&&jsonObject.has("email")) {
				PlayerDatabaseAccess playerDatabaseAccess = new PlayerDatabaseAccess();
				Player player = playerDatabaseAccess.getPlayerByEmail(jsonObject.getString("email"));
				if (player!=null) {
					response.setStatus(HttpServletResponse.SC_FOUND);
				} else {
					player = new Player(jsonObject.getString("playerName"), jsonObject.getString("email"));
					playerDatabaseAccess.putPlayer(player);
					response.setStatus(HttpServletResponse.SC_CREATED);
				}
				JSONObject playerJson = new JSONObject(player);
				playerJson.remove("code");
				response.getWriter().append(playerJson.toString());
			}
		} catch (ParseException e) {
			throw new IOException("Error parsing JSON request string");
		}
		response.setContentType("application/json");
	}

}
