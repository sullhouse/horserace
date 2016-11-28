package com.sullhouse.gambol;

import org.json.JSONObject;

public class PlayerBet {
	private Player player;
	private char horse;
	private double bet;
	
	public PlayerBet (Player player, char horse) {
		this.player = player;
		this.horse = horse;
	}
	public PlayerBet(char horse, double bet, Player player) {
		this.player = player;
		this.horse = horse;
		this.bet = bet;
	}
	public PlayerBet(JSONObject playerBetObject) {
		this.horse = playerBetObject.getString("horse").charAt(0);
		this.bet = playerBetObject.getDouble("bet");
		this.player = new Player(playerBetObject.getJSONObject("player"));
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public char getHorse() {
		return horse;
	}
	public void setHorse(char horse) {
		this.horse = horse;
	}
	public double getBet() {
		return bet;
	}
	public void setBet(double bet) {
		this.bet = bet;
	}
	public void addBet(double bet) {
		this.bet += bet;
	}
}
