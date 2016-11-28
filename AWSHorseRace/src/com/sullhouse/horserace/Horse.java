package com.sullhouse.horserace;

import com.sullhouse.deck.Card;

public class Horse {
	private Card card;
	private int distance = 0;
	
	public Horse(Card card) {
		this.card = card;
	}
	
	public void advance() {
		this.distance++;
	}
	
	public void comeBack() {
		this.distance--;
	}

	public int getDistance() {
		return distance;
	}
	
	public String getName() {
		return card.getSuitName();
	}

	public Card getCard() {
		return card;
	}
}
