package com.sullhouse.gambol;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.ParseException;
import org.json.JSONObject;

import com.sullhouse.horserace.HorseRaceDatabaseAccess;

public class JoinHorseRaceAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public JoinHorseRaceAPI() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String email = request.getParameter("email");
	    String createPlayer = request.getParameter("createPlayer");
	    String horseRaceId = request.getParameter("horseRaceId");
	    
	    if (createPlayer.equals("false")) {
	    	PlayerDatabaseAccess playerDatabaseAccess = new PlayerDatabaseAccess();
	    	Player player = playerDatabaseAccess.getPlayerByEmail(email);
			if (player!=null) {
				EmailSender emailSender = new EmailSender();
				
				String subject = "Horse Race " + horseRaceId + "!!!!";
				
				HorseRaceDatabaseAccess horseRaceDatabaseAccess = new HorseRaceDatabaseAccess();
				
				JSONObject horseRaceJson = horseRaceDatabaseAccess.getHorseRace(horseRaceId);
				
				String configHtml = "<h3>A new horse race has been scheduled!</h3>"
						+ "Length: " + horseRaceJson.getInt("horseRaceLength");
				
				Date startTime = getDateFromString(horseRaceJson.getString("startTime"));
				
				configHtml += "<br>Start Time: " + startTime.toString();
				
				emailSender.sendEmail(email, subject, configHtml + player.getBettingLinksHTML(horseRaceId));
				
				response.getWriter().append("<html><body>");
				
				response.getWriter().append("You've added " + email + " to the horse race! They should have gotten an email.<br><br>Tell them to place their bets now!!!");
				
				response.getWriter().append("</body></html>");
				
				response.setContentType("text/html");
			} else {
				response.sendRedirect("joinhorseracenewplayer.jsp?horseRaceId=" + horseRaceId +"&email=" + email);
			}
			
	    } else {
	    	String playerName = request.getParameter("playerName");
	    	Player player = new Player(playerName, email);
	    	PlayerDatabaseAccess playerDatabaseAccess = new PlayerDatabaseAccess();
	    	playerDatabaseAccess.putPlayer(player);
	    	
	    	EmailSender emailSender = new EmailSender();
			
			String subject = "Horse Race " + horseRaceId + "!!!!";
			
			HorseRaceDatabaseAccess horseRaceDatabaseAccess = new HorseRaceDatabaseAccess();
			
			JSONObject horseRaceJson = horseRaceDatabaseAccess.getHorseRace(horseRaceId);
			
			String configHtml = "<h3>A new horse race has been scheduled!</h3>"
					+ "Length: " + horseRaceJson.getInt("horseRaceLength");
			
			Date startTime = getDateFromString(horseRaceJson.getString("startTime"));
			
			configHtml += "<br>Start Time: " + startTime.toString();
			
			emailSender.sendEmail(email, subject, configHtml + player.getBettingLinksHTML(horseRaceId));
			
			response.getWriter().append("<html><body>");
			
			response.getWriter().append("You've added " + email + " to the horse race! They should have gotten an email.<br><br>Tell them to place their bets now!!!");
			
			response.getWriter().append("</body></html>");
			
			response.setContentType("text/html");
	    }
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
