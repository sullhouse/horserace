package com.sullhouse.gambol;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class PlayerBetAPI extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PlayerBetAPI() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String code;
		double bet;
		String horseRaceId = null;
		Player player = null;
		char horse = 0;
		boolean needPlayerName = false;
		if (request.getParameter("code")!=null) {
			code = request.getParameter("code");
			PlayerDatabaseAccess playerDatabaseAccess = new PlayerDatabaseAccess();
			player = playerDatabaseAccess.getPlayer(code);
			if (player==null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().append("{\"error\" : \"Player code not found\" }");
				return;
			}
			if (player.getPlayerName().equals("NEED_PLAYER_NAME")) needPlayerName = true;
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().append("{\"error\" : \"Player code required\" }");
			return;
		}
		
		if (request.getParameter("bet")!=null) {
			bet = Double.parseDouble(request.getParameter("bet"));
		} else {
			bet = 1;
		}
		
		if (request.getParameter("horseRaceId")!=null) {
			horseRaceId = request.getParameter("horseRaceId");
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().append("{\"error\" : \"HorseRaceId required\" }");
			return;
		}
		
		if (request.getParameter("horse")!=null) {
			horse = request.getParameter("horse").toUpperCase().charAt(0);
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().append("{\"error\" : \"Horse required\" }");
			return;
		}
		
		if (!needPlayerName) {
			HorseRaceGambol horseRaceGambol = new HorseRaceGambol(horseRaceId);
			
			horseRaceGambol.addPlayerBet(player, horse, bet);
			
			horseRaceGambol.persist();
			
			response.getWriter().append("<html><body>");
			
			response.getWriter().append("You've bet $1 on " + horse + "!<br>"
					+ "\nYour total bets are now:<br>"
					+ horseRaceGambol.playerTotalBetsHTML(player));
			
			response.getWriter().append("<br><br>Bet More!!!!"
					+ player.getBettingLinksHTML(horseRaceId));
			
			response.getWriter().append("</body></html>");
			
			
			response.setContentType("text/html");
		} else {
			response.sendRedirect("getplayername.jsp?horseRaceId=" + horseRaceId +"&email=" + player.getEmail() + "&bet=" + bet + "&horse=" + horse);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String playerName = request.getParameter("playerName");
		String code = request.getParameter("code");
		String email = request.getParameter("email");
    	Player player = new Player(playerName, email, code);

    	PlayerDatabaseAccess playerDatabaseAccess = new PlayerDatabaseAccess();
    	playerDatabaseAccess.putPlayer(player);
		doGet(request, response);
	}

}
