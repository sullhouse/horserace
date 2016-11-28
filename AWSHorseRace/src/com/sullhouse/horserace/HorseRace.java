package com.sullhouse.horserace;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sullhouse.deck.*;

public class HorseRace {
	private Deck deck = new Deck();
	private List<Horse> horses = new ArrayList<Horse>();
	private Horse winningHorse;
	private int length;
	private Stands stands = new Stands();
	private boolean gameOver = false;
	private String whatJustHappened;
	private String log;
	private String id;
	private boolean raceStarted;
	
	public HorseRace(int length) {
		this.length = length;
		this.id = new SimpleDateFormat("ddMMyyhhmmss").format(new Date());
		setHorses();
		
		deck.shuffle();
		
		setStands();
		
		this.raceStarted = false;
		
		this.whatJustHappened = "\tHorses are set and ready to race!";
		System.out.println(this);
		this.addToLog(this.toString());
	}

	private void setStands() {
		for (int i=0;i<length;i++) {
			stands.addCard(deck.drawCard());
		}
	}

	private void setHorses() {
		horses.add(new Horse(deck.pullCard(1, 'C')));
		horses.add(new Horse(deck.pullCard(1, 'D')));
		horses.add(new Horse(deck.pullCard(1, 'H')));
		horses.add(new Horse(deck.pullCard(1, 'S')));
		// System.out.println("HorseRace: Pulled and set the horses");
	}

	public int getLength() {
		return length;
	}

	public Deck getDeck() {
		return deck;
	}

	public boolean isGameOver() {
		return gameOver;
	}
	
	public void flipCard() {
		this.raceStarted = true;
		whatJustHappened = "";
		if (deck.isDeckEmpty()) {
			deck.reShuffle();
			whatJustHappened += "Reshuffling the deck...\n";
		} else {
			Card card = deck.drawCard();
			// System.out.println("HorseRace: Drew the " + card.getLongName());
			whatJustHappened += "Drew the " + card.getLongName();
			for (Horse h : horses) {
				if (h.getCard().getSuit()==card.getSuit()) {
					h.advance();
					// System.out.println("HorseRace: " + h.getName() + " advances and is now at " + h.getDistance() + "...");
					whatJustHappened += "\n" + h.getName() + " advances and is now at " + h.getDistance() + "...";
					if (h.getDistance()>this.length) {
						this.gameOver = true;
						this.winningHorse = h;
						// System.out.println("HorseRace: " + h.getName() + " wins!");
						whatJustHappened += "\n" + h.getName() + " wins!";
					}
					if (this.getLastHorseDistance()-1>stands.getNumFlippedCards()) {
						Card standCard = stands.flipCard();
						// System.out.println("HorseRace: the stands go wild with the " + standCard.getLongName());
						whatJustHappened += "\nThe stands go wild with the " + standCard.getLongName();
						for (Horse k : horses) {
							if (k.getCard().getSuit()==standCard.getSuit()) {
								k.comeBack();
								// System.out.println("HorseRace: " + k.getName() + " moves back and is now at " + k.getDistance() + "...");
								whatJustHappened += "\n" + k.getName() + " moves back and is now at " + k.getDistance() + "...";
							}
						}
					}
					break;
				}
			}
		}
		System.out.println(this);
		this.addToLog(this.toString());
	}

	public Horse getWinningHorse() {
		return winningHorse;
	}

	public void setWinningHorse(Horse winningHorse) {
		this.winningHorse = winningHorse;
	}

	public int getLastHorseDistance() {
		int lastHorseDistance = length+1;
		for (Horse h : horses) {
			if (h.getDistance() < lastHorseDistance) lastHorseDistance = h.getDistance(); 
		}
		return lastHorseDistance;
	}
	
	public String toString() {
		String s = "";
		
		int i = length+1;
		
		while (i>=0) {
			s+="\n";
			if (stands.getNumFlippedCards()>=i&&i>0&&i<=length) {
				s+="  " + stands.getCards().get(i-1).getShortName() + "  ";
			} else if (i>0&&i<=length) {
				s+="  XX  ";
			}
			for (Horse h : horses) {
				if (h.getDistance()==i) {
					s+= "\t" + h.getCard().getSuit() + " ";
				} else {
					s+= "\t  ";
				}
			}
			s+="\n";
			i--;
		}
		
		s+="\nAnnouncer:\n" + 
				whatJustHappened +
				"\n-------------------------------------------------";
		return s;
	}
	
	public String toHTML() {
		String s = "<table>";
		
		int i = length+1;
		
		while (i>=0) {
			s += "<tr><td width=70>";
			if (stands.getNumFlippedCards()>=i&&i>0&&i<=length) {
				s += stands.getCards().get(i-1).getShortName();
			} else if (i>0&&i<=length) {
				s += "XX";
			}
			s += "</td>";
			for (Horse h : horses) {
				s += "<td width=70>";
				if (h.getDistance()==i) s+= h.getCard().getSuit();
				s += "</td>";
			}
			s+="</tr>";
			i--;
		}
		
		s += "<tr></tr><tr><td colspan=5>Announcer:</td></tr>";
		
		String[] wjh = whatJustHappened.split("\n");
		
		for (String w : wjh) {
			s += "<tr><td colspan=5 align=center>" + w + "</td></tr>";
		}
		s += "<tr><td colspan=5 align='center'>-------------------------------------------------</td></tr>";
		s += "</table>";
		return s;
	}

	public String getLog() {
		return log;
	}

	public void addToLog(String log) {
		this.log += log;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isRaceStarted() {
		return raceStarted;
	}

	public void setRaceStarted(boolean raceStarted) {
		this.raceStarted = raceStarted;
	}
}
