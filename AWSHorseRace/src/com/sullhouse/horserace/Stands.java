package com.sullhouse.horserace;

import java.util.ArrayList;
import java.util.List;

import com.sullhouse.deck.Card;

public class Stands {
	private List<Card> cards = new ArrayList<Card>();
	private int numFlippedCards = 0;
	private int numCards = 0;

	public Stands() {
	}
	
	public void addCard(Card card) {
		cards.add(card);
		this.numCards++;
	}
	
	public Card flipCard() {
		this.numFlippedCards++;
		return cards.get(numFlippedCards-1);
	}
	
	public int getNumFlippedCards() {
		return numFlippedCards;
	}

	public int getNumCards() {
		return numCards;
	}
	
	public List<Card> getCards() {
		return cards;
	}
}
