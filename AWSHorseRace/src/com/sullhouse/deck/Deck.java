package com.sullhouse.deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
	private List<Card> cards = new ArrayList<Card>();
	private int remainingCards = 52;
	private int pulledCards = 0;
	private int topCard = -1;
	
	public Deck() {
		super();
		
		int i,j;
		char[] suit = {'C','D','H','S'};
		
		for (i=1;i<14;i++) {
			for (j=0;j<4;j++) {
				Card card = new Card(i,suit[j]);
				cards.add(card);
			}
		}
	}
	
	public int getRemainingCards() {
		return remainingCards;
	}
	
	public void setRemainingCards(int remainingCards) {
		this.remainingCards = remainingCards;
	}
	
	public List<Card> getCards() {
		return cards;
	}

	public void setCards(List<Card> cards) {
		this.cards = cards;
	}
	
	public int getPulledCards() {
		return pulledCards;
	}

	public void setPulledCards(int pulledCards) {
		this.pulledCards = pulledCards;
	}
	
	public int getTopCard() {
		return topCard;
	}

	public void setTopCard(int topCard) {
		this.topCard = topCard;
	}

	public Card drawCard() {
		if (remainingCards==0) {
			return null;
		} else {
			this.remainingCards--;
			this.topCard++;
			return this.cards.get(topCard);
		}
	}

	public String toString() {
		return "Deck [cards=" + cards + "]";
	}

	public void shuffle() {
		long seed = System.nanoTime();
		Collections.shuffle(cards, new Random(seed));
	}
	
	public void reShuffle() {
		this.remainingCards = 52 - this.pulledCards;
		this.topCard = -1;
		long seed = System.nanoTime();
		Collections.shuffle(cards, new Random(seed));
	}
	
	public Card pullCard(int rank, char suit) {
		for (Card c : cards) {
			if (c.getRank()==rank&&c.getSuit()==suit) {
				this.remainingCards--;
				this.pulledCards++;
				cards.remove(c);
				return c;
			}
		}
		return null;
	}
	
	public boolean isDeckEmpty() {
		if (remainingCards>0) {
			return false;
		} else {
			return true;
		}
	}
}
