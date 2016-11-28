package com.sullhouse.deck;

public class Card {
	private int rank;
	private char suit;
	
	public Card(int rank, char suit) {
		super();
		this.rank = rank;
		this.suit = suit;

	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public char getSuit() {
		return suit;
	}
	public void setSuit(char suit) {
		this.suit = suit;
	}
	public String getShortName() {
		if (rank>1&&rank<10) {
			return rank + "" + suit;
		} else {
			switch (rank) {
				case 1:
					return "A" + suit;
				case 10:
					return "T" + suit;
				case 11:
					return "J" + suit;
				case 12:
					return "Q" + suit;
				case 13:
					return "K" + suit;
			}
		}
		return null;
	}

	public String getLongName() {
		return this.getRankName() + " of " + this.getSuitName();
	}
	
	public String getSuitName() {
		switch (suit) {
			case 'C':
				return "Clubs";
			case 'D':
				return "Diamonds";
			case 'H':
				return "Hearts";
			case 'S':
				return "Spades";
		}
		return null;
	}
	public String toString() {
		return this.getShortName();
	}
	
	public String getRankName() {
		switch (rank) {
			case 1:
				return "Ace";
			case 2:
				return "Two";
			case 3:
				return "Three";
			case 4:
				return "Four";
			case 5:
				return "Five";
			case 6:
				return "Six";
			case 7:
				return "Seven";
			case 8:
				return "Eight";
			case 9:
				return "Nine";
			case 10:
				return "Ten";
			case 11:
				return "Jack";
			case 12:
				return "Queen";
			case 13:
				return "King";
		}
		return null;
	}

}
