package com.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Deck {
	private List<Card> cards;
    private Random random;

    public Deck() {
        this.cards = new ArrayList<>();
        this.random = new Random();

        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Value value : Card.Value.values()) {
                cards.add(new Card(suit, value));
            }
        }
    }
    public void createScenario() {
    	cards.set(1, new Card(Card.Suit.CLUBS, Card.Value.ACE));
    	cards.set(3, new Card(Card.Suit.CLUBS, Card.Value.NINE));
    }
    public void shuffle() {
        Collections.shuffle(cards, random);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("Deck is empty. Cannot deal a card.");
        }
        return cards.remove(0);
    }
}

