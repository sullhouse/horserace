package com.sullhouse.horserace;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sullhouse.gambol.Player;
import com.sullhouse.gambol.PlayerDatabaseAccess;

public class HorseRaceAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public HorseRaceAPI() {
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		HorseRaceDatabaseAccess horseRaceDatabaseAccess = new HorseRaceDatabaseAccess();
		if (request.getPathInfo()!=null) {
			String id = request.getPathInfo().substring(1);
			JSONObject horseRace = horseRaceDatabaseAccess.getHorseRace(id);
			if (horseRace!=null) {
				response.getWriter().append(horseRace.toString());
				response.setStatus(HttpServletResponse.SC_FOUND);
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} else {
			response.getWriter().append(horseRaceDatabaseAccess.getAllHorseRaces().toString());
			response.setStatus(HttpServletResponse.SC_OK);
		}
		
		response.setContentType("application/json");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuffer jb = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) jb.append(line);
		} catch (Exception e) { 
			/*report an error*/ 
		}
		
		int length = 6;
		int secondsDelay = 60;
		List<Player> players = new ArrayList<Player>();
		Calendar cDate = Calendar.getInstance();
		long t = cDate.getTimeInMillis();
		Date startTime = new Date(t + 60000);
		try {
			JSONObject jsonObject = new JSONObject(jb.toString());
			if (jsonObject.has("length")) length = jsonObject.getInt("length");
			if (jsonObject.has("secondsDelay")) secondsDelay = jsonObject.getInt("secondsDelay");
			if (jsonObject.has("players")) {
				PlayerDatabaseAccess playerDatabaseAccess = new PlayerDatabaseAccess();
				JSONArray jsonPlayers = jsonObject.getJSONArray("players");
				if (jsonPlayers.length()>0) {
					for (int i=0;i<jsonPlayers.length();i++) {
						JSONObject playerJson = jsonPlayers.getJSONObject(i);
						String email = playerJson.getString("email");
						Player player = playerDatabaseAccess.getPlayerByEmail(email);
						players.add(player);
					}
				}
			}
			if (jsonObject.has("startTime")) startTime = getDateFromString(jsonObject.getString("startTime"));			
			
			HorseRaceRunner horseRaceRunner = new HorseRaceRunner(length, secondsDelay, startTime, players);
			
			JSONObject horseRaceJson = new JSONObject();
			horseRaceJson.append("length", length);
			horseRaceJson.append("secondsDelay", secondsDelay);
			horseRaceJson.append("startTime", startTime.toString());
			horseRaceJson.append("id", horseRaceRunner.getHorseRace().getId());
			horseRaceJson.append("players", new JSONArray(players));
			
			response.getWriter().append(horseRaceJson.toString());
			response.setStatus(HttpServletResponse.SC_CREATED);
			
		} catch (ParseException e) {
			throw new IOException("Error parsing JSON request string");
		}
		response.setContentType("application/json");
	}
	
	private Date getDateFromString(String s) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		TimeZone tz = TimeZone.getTimeZone("EST");
		formatter.setTimeZone(tz);
		String dateInString = s;
	
		try {
		    Date date = formatter.parse(dateInString);
		    return date;
		} catch (ParseException e) {
		    e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}


