package com.sullhouse.gambol;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.sullhouse.horserace.HorseRaceDatabaseAccess;

public class HorseRaceGambol {
	private List<PlayerBet> bets = new ArrayList<PlayerBet>();
	private String horseRaceId;
	private boolean bettingOpen;
	
	public List<PlayerBet> getBets() {
		return bets;
	}
	
	public HorseRaceGambol(String horseRaceId) {
		this.horseRaceId = horseRaceId;
		HorseRaceDatabaseAccess horseRaceDatabaseAccess = new HorseRaceDatabaseAccess();
		
		if (horseRaceDatabaseAccess.checkExists(horseRaceId)) {
			this.bets = horseRaceDatabaseAccess.getBets(horseRaceId);
			this.bettingOpen = horseRaceDatabaseAccess.getIsBettingOpen(horseRaceId);
		}
	}
	
	public void addPlayerBet(Player player, char horse, double bet) {
		if (this.bettingOpen){
			for (PlayerBet b : bets) {
				if (b.getPlayer().getEmail().equalsIgnoreCase(player.getEmail())&&b.getHorse()==horse) {
					b.addBet(bet);
					return;
				}
			}
			
			PlayerBet playerBet = new PlayerBet(horse, bet, player);
			this.bets.add(playerBet);
		}
	}
	
	public void persist() {
		HorseRaceDatabaseAccess horseRaceDatabaseAccess = new HorseRaceDatabaseAccess();
		if (horseRaceDatabaseAccess.checkExists(horseRaceId)) {
			horseRaceDatabaseAccess.setBets(this);
		}		
	}

	public String getHorseRaceId() {
		return horseRaceId;
	}

	public void setHorseRaceId(String horseRaceId) {
		this.horseRaceId = horseRaceId;
	}
	
	public String playerTotalBetsHTML (Player player) {
		String s = "";
		NumberFormat formatter = new DecimalFormat("#0.00"); 
		for (PlayerBet b : bets) {
			if (b.getPlayer().getEmail().equalsIgnoreCase(player.getEmail())) {
				s += "<br>" + b.getHorse() + ": $" + formatter.format(b.getBet());
			}
		}
		return s;
	}
	
	public String getPayoutsByHorse() {
		double c = 0,d = 0,h = 0,s = 0;
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		String p = "<table><tr><th>Player</th><th>Suit</th><th>Paid</th><th>To Win</th></tr>";
		for (PlayerBet b : bets) {
			switch (b.getHorse()) {
				case 'C':
					c += b.getBet();
					break;
				case 'D':
					d += b.getBet();
					break;
				case 'H':
					h += b.getBet();
					break;
				case 'S':
					s += b.getBet();
					break;
			}
		}
		double total = c + d + h + s;
		for (PlayerBet b : bets) {
			double toWin = 0;
			switch (b.getHorse()) {
				case 'C':
					toWin = (b.getBet()/c) * total;
					break;
				case 'D':
					toWin = (b.getBet()/d) * total;
					break;
				case 'H':
					toWin = (b.getBet()/h) * total;
					break;
				case 'S':
					toWin = (b.getBet()/s) * total;
					break;
			}
			p += "<tr><td>" + b.getPlayer().getPlayerName() + "</td><td>" + b.getHorse() + "</td><td>" + formatter.format(b.getBet()) + "</td><td>" + formatter.format(toWin) + "</td></tr>";
		}
		
		p += "</table>";
		
		return p;
	}
}
