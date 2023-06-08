package com.blackjack;


public class Card {
	public enum Suit { CLUBS, DIAMONDS, HEARTS, SPADES }
    public enum Value { TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10),
                        JACK(10), QUEEN(10), KING(10), ACE(11);

        private int numericValue;

        Value(int numericValue) {
            this.numericValue = numericValue;
        }

        public int getNumericValue() {
            return numericValue;
        }
    }

    private Suit suit;
    private Value value;

    public Card(Suit suit, Value value) {
        this.suit = suit;
        this.value = value;
    }

    public Suit getSuit() {
        return suit;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value + " of " + suit;
    }
}


