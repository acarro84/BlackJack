package com.blackjack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Game {

	private static final int INITIAL_BALANCE = 100;

	private static final int BET_MIN = 10;

	private static final int BLACKJACK_VALUE = 21;

	private static final int DEALER_MINIMUM = 17;

	private static final String PLAY_AGAIN_PROMPT = "Do you want to play again? (y/n): ";

	private static final String WIN_MESSAGE = "Congratulations! You won!";

	private static final String LOSE_MESSAGE = "Sorry! You lost!";

	private static final String PUSH_MESSAGE = "It's a push!";

	private Scanner scanner;
	private Player player;
	private House house;
	private Deck deck;
	int bet;

	public Game() {
		this.scanner = new Scanner(System.in);
		this.player = null;
		this.house = new House();
		this.deck = new Deck();
		deck.shuffle();
		
		
	}

	public void start() {
		System.out.println("Welcome to the Blackjack game!");

		String playerName = getPlayerName();
		player = new Player(playerName);
		player.setEarnings(INITIAL_BALANCE);

		boolean continuePlaying = true;
		while (continuePlaying) {
			playRound();
			continuePlaying = askToPlayAgain();
			deck.shuffle();
		}

		System.out.println("Thank you for playing!");
	}

	private String getPlayerName() {
		System.out.print("Please enter your name: ");
		return scanner.nextLine();
	}

	private void playRound() {
		System.out.println("\n--- Round Start ---");
		player.clearHand();
		house.clearHand();

		if (player.getEarnings() <= 0) {
			System.out.println("Insufficient earnings to place a bet. Game over!");
			return;
		}

		int bet = placeBet();
		dealFirstCards();

		if (player.calculateHandValue() == BLACKJACK_VALUE) {
			System.out.println("Blackjack! You win!");
			player.setEarnings(player.getEarnings() + (int) (bet * 1.5));
		} else {
			boolean flag = insurance();
			if (!flag) {
				playerTurn();
				houseTurn();
				determineWinner();
			}
		}

		System.out.println("Your earnings: $" + player.getEarnings());
	}

	public int placeBet() {
		bet = 0;
		boolean validBet = false;

		while (!validBet) {
			System.out.println("Your earnings: $" + player.getEarnings());
			System.out.print("Place your bet ($" + BET_MIN + " minimum): ");
			String input = scanner.nextLine();

			try {
				bet = Integer.parseInt(input);
				if (bet >= BET_MIN && bet <= player.getEarnings()) {
					validBet = true;
				} else {
					System.out.println("Invalid bet amount. Please try again.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid bet amount. Please try again.");
			}
		}

		player.setEarnings(player.getEarnings() - bet);
		return bet;
	}

	private void dealFirstCards() {
		for (int i = 0; i < 2; i++) {
			player.addCardToHand(deck.dealCard());
			house.addCardToHand(deck.dealCard());
		}

		System.out.println("Your cards: " + player.getHand());
		System.out.println("House cards: " + house.getHand().get(0) + " and [Hidden]");
	}
	public boolean insurance() {
		if(house.getHand().get(0).getValue() == Card.Value.ACE) {
			System.out.println("Would you like to purchase insurance for the dealer's Ace?");
			if (scanner.nextLine().toLowerCase().contains("y")) {
				player.setEarnings(player.getEarnings() - bet/2);
				if (house.calculateHandValue() == 21) {
					player.setEarnings(player.getEarnings() + bet);
					System.out.println("Dealer had Black Jack. Your insurance bet was returned.");
					return true;
				} 
			}
		} return false;
	}
	
	

	public void playerTurn() {
		while (true) {
			System.out.print("Do you want to hit or stay? (h/s): ");
			String choice = scanner.nextLine();

			if (choice.equalsIgnoreCase("h")) {
				Card card = deck.dealCard();
				player.addCardToHand(card);
				System.out.println("You drew a " + card);
				System.out.println("Your cards: " + player.getHand());

				int handValue = player.calculateHandValue();
				if (handValue > BLACKJACK_VALUE) {
					System.out.println("Busted! You lose.");
					break;
				} else if (handValue == BLACKJACK_VALUE) {
					System.out.println("You have 21!");
					break;
				}
			} else if (choice.equalsIgnoreCase("s")) {
				System.out.println("You chose to stay.");
				break;
			} else {
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private void houseTurn() {
		System.out.println("House cards: " + house.getHand());

		while (house.calculateHandValue() < DEALER_MINIMUM) {
			Card card = deck.dealCard();
			house.addCardToHand(card);
			System.out.println("House drew a " + card);
			System.out.println("House cards: " + house.getHand());
		}
	}

		
	private void determineWinner() {
        int playerHandValue = player.calculateHandValue();
        int houseHandValue = house.calculateHandValue();

        System.out.println("Your hand value: " + playerHandValue);
        System.out.println("House hand value: " + houseHandValue);

        if (playerHandValue > BLACKJACK_VALUE) {
            System.out.println(LOSE_MESSAGE);
            writeGameData(player.getName(), "Loss");
        } else if (houseHandValue > BLACKJACK_VALUE) {
            System.out.println(WIN_MESSAGE);
            player.setEarnings(player.getEarnings() + 2 * bet);
            writeGameData(player.getName(), "Win");
        } else if (playerHandValue > houseHandValue) {
            System.out.println(WIN_MESSAGE);
            player.setEarnings(player.getEarnings() + 2 * bet);
            writeGameData(player.getName(), "Win");
        } else if (playerHandValue < houseHandValue) {
            System.out.println(LOSE_MESSAGE);
            writeGameData(player.getName(), "Loss");
        } else {
            System.out.println(PUSH_MESSAGE);
            player.setEarnings(player.getEarnings() + BET_MIN);
            writeGameData(player.getName(), "Push");
        }
    }
     private void writeGameData(String playerName, String result) {
            try {
                String fileName = "src\\game_data.txt";
                File file = new File(fileName);
                FileWriter writer = new FileWriter(file, true);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String timestamp = dateFormat.format(new Date());

                writer.write(timestamp + " - Player: " + playerName + ", Result: " + result + "\n");
                writer.close();
            } catch (IOException e) {
                System.out.println("Failed to write game data.");
            }
        }

	private boolean askToPlayAgain() {
		while (true) {
			System.out.print(PLAY_AGAIN_PROMPT);
			String choice = scanner.nextLine();

			if (choice.equalsIgnoreCase("y")) {
				return true;
			} else if (choice.equalsIgnoreCase("n")) {
				return false;
			} else {
				System.out.println("Invalid choice. Please try again.");
			}
		}
	}

}
