package com.sullhouse.gambol;

import org.json.JSONObject;

public class Player {
	private String playerName;
	private String email;
	private String code;
	private static String host = "horserace.sullhouse.com";

	public Player(String name, String email) {
		super();
		this.playerName = name;
		this.email = email;
		this.code = java.util.UUID.randomUUID().toString();
	}

	public Player(JSONObject jsonObject) {
		this.playerName = jsonObject.getString("playerName");
		this.email = jsonObject.getString("email");
		this.code = jsonObject.getString("code");
	}

	public Player(String name, String email, String code) {
		super();
		this.playerName = name;
		this.email = email;
		this.code = code;
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCode() {
		return code;
	}	
	
	public String getBettingLinksHTML(String horseRaceId) {
		String s = "<br><br>";
		
		s += "<a href=\"http://" + host + "/playerbet?code=" + this.code + "&horseRaceId=" + horseRaceId + "&horse=C\">Click here to bet $1 on Clubs</a><br><br>";
		s += "<a href=\"http://" + host + "/playerbet?code=" + this.code + "&horseRaceId=" + horseRaceId + "&horse=D\">Click here to bet $1 on Diamonds</a><br><br>";
		s += "<a href=\"http://" + host + "/playerbet?code=" + this.code + "&horseRaceId=" + horseRaceId + "&horse=H\">Click here to bet $1 on Hearts</a><br><br>";
		s += "<a href=\"http://" + host + "/playerbet?code=" + this.code + "&horseRaceId=" + horseRaceId + "&horse=S\">Click here to bet $1 on Spades</a><br><br>";
		s += "<p>Do not share these links! To invite more players to this horse race, <a href=\"http://" + host + "/joinhorserace.jsp?code=" + this.code + "&horseRaceId=" + horseRaceId + "\">click here.</p>";
		return s;
	}
}
