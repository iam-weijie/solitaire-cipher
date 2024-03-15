package cipher;

import java.util.Random;

public class Deck {
	public static String[] suitsInOrder = {"clubs", "diamonds", "hearts", "spades"};
	public static Random gen = new Random();

	public int numOfCards; // contains the total number of cards in the deck
	public Card head; // contains a pointer to the card on the top of the deck

	/* 
	 * TODO: Initializes a Deck object using the inputs provided
	 */
	public Deck(int numOfCardsPerSuit, int numOfSuits) {
		if (numOfCardsPerSuit > 13 || numOfCardsPerSuit < 1 || numOfSuits < 1 || numOfSuits > suitsInOrder.length)
			throw new IllegalArgumentException("Invalid input parameters");

		for (int i = 0; i < numOfSuits; i++) {
			for (int r = 1; r <= numOfCardsPerSuit; r++) {
				PlayingCard newCard = new PlayingCard(suitsInOrder[i], r);
				addCard(newCard);
			}
		}

		addCard(new Joker("red"));
		addCard(new Joker("black"));
	}

	/* 
	 * TODO: Implements a copy constructor for Deck using Card.getCopy().
	 * This method runs in O(n), where n is the number of cards in d.
	 */
	public Deck(Deck d) {
		if (d == null)
			throw new IllegalArgumentException("Input deck cannot be null");
		
		Card current = d.head;
		for (int i = 0; i < d.numOfCards; i++) {
			Card copy = current.getCopy();
			addCard(copy);
			current = current.next;
		}
	}

	/*
	 * For testing purposes we need a default constructor.
	 */
	public Deck() {}

	/* 
	 * TODO: Adds the specified card at the bottom of the deck. This 
	 * method runs in $O(1)$. 
	 */
	public void addCard(Card c) {
		if (head == null) {
			head = c;
			c.next = c;
			c.prev = c;
		} else {
			Card tail = head.prev;
			tail.next = c;
			c.prev = tail;
			c.next = head;
			head.prev = c;
		}
		numOfCards++;
	}

	/*
	 * TODO: Shuffles the deck using the algorithm described in the pdf. 
	 * This method runs in O(n) and uses O(n) space, where n is the total 
	 * number of cards in the deck.
	 */
	public void shuffle() {
		if (numOfCards == 0)
			return;

		Card[] array = new Card[numOfCards];
		int index = 0;
		Card current = head;
		do {
			array[index++] = current;
			current = current.next;
		} while (current != head);

		for (int i = numOfCards - 1; i > 0; i--) {
			int j = gen.nextInt(i + 1);
			
			//swap
			Card temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}

		head = array[0];
		current = head;
		for (int i = 1; i < numOfCards; i++) {
			current.next = array[i];
			array[i].prev = current;
			current = current.next;
		}
		current.next = head;
		head.prev = current;
	}

	/*
	 * TODO: Returns a reference to the joker with the specified color in 
	 * the deck. This method runs in O(n), where n is the total number of 
	 * cards in the deck. 
	 */
	public Joker locateJoker(String color) {
		if (color.equalsIgnoreCase("red") || color.equalsIgnoreCase("black")) {
			Card current = head;
			do {
				if (current instanceof Joker && ((Joker) current).getColor().equalsIgnoreCase(color)) {
					return (Joker) current;
				}
				current = current.next;
			} while(current != head);
		}
		return null;
	}

	/*
	 * TODO: Moved the specified Card, p positions down the deck. You can 
	 * assume that the input Card does belong to the deck (hence the deck is
	 * not empty). This method runs in O(p).
	 */
	public void moveCard(Card c, int p) {
		Card current = c;
		p = p % (numOfCards - 1);

		if (p == 0)
			return;
			
		for (int i = 0; i < p; i++) {
			current = current.next;
		}

		c.prev.next = c.next;
        c.next.prev = c.prev;
		
		c.next = current.next;
		c.prev = current;
		current.next.prev = c;
		current.next = c;
	}

	/*
	 * TODO: Performs a triple cut on the deck using the two input cards. You 
	 * can assume that the input cards belong to the deck and the first one is 
	 * nearest to the top of the deck. This method runs in O(1)
	 */
	public void tripleCut(Card firstCard, Card secondCard) {
		Card temp1 = firstCard.prev;
		Card temp2 = secondCard.next;
		Card tail = head.prev;

		if (firstCard == head) {
			head = temp2;
		} else if (secondCard == tail) {
			head = firstCard;
		} else {
			head.prev = secondCard;
			secondCard.next = head;
			firstCard.prev = tail;
			tail.next = firstCard;
			head = temp2;
			head.prev = temp1;
			temp1.next = head;
		}
	}

	/*
	 * TODO: Performs a count cut on the deck. Note that if the value of the 
	 * bottom card is equal to a multiple of the number of cards in the deck, 
	 * then the method should not do anything. This method runs in O(n).
	 */
	public void countCut() {
		Card current = head;
		Card tail = head.prev;

		int bottom_number = tail.getValue();
		bottom_number = bottom_number % numOfCards;

		if (bottom_number == 0 || bottom_number == numOfCards - 1)
			return;
		else {
			for(int i = 1; i < bottom_number; i++) {
				current = current.next;
			}

			tail.prev.next = head;
			head.prev = tail.prev;
			head = current.next;
			current.next = tail;
			tail.prev = current;
			tail.next = head;
			head.prev = tail;
		}
	}

	/*
	 * TODO: Returns the card that can be found by looking at the value of the 
	 * card on the top of the deck, and counting down that many cards. If the 
	 * card found is a Joker, then the method returns null, otherwise it returns
	 * the Card found. This method runs in O(n).
	 */
	public Card lookUpCard() {
		Card current = head;
		int count = head.getValue();

		for (int i = 0; i < count; i++) {
			current = current.next;
		}
		
		if (current instanceof Joker)
			return null;
		else
			return current;
	}

	/*
	 * TODO: Uses the Solitaire algorithm to generate one value for the keystream 
	 * using this deck. This method runs in O(n).
	 */
	public int generateNextKeystreamValue() {
		Card foundCard;		
		do {
			Joker RJ = locateJoker("red");
			moveCard(RJ, 1);
			
			Joker BJ = locateJoker("black");
			moveCard(BJ, 2);

			Card current = head;
			
			while (!(current instanceof Joker))
				current = current.next;
			
			if (current == RJ) 
				tripleCut(RJ, BJ);
			else
				tripleCut(BJ, RJ);

			countCut();

			foundCard = lookUpCard();
		} while (foundCard == null);
		
		return foundCard.getValue();
	}


	public abstract class Card { 
		public Card next;
		public Card prev;

		public abstract Card getCopy();
		public abstract int getValue();

	}

	public class PlayingCard extends Card {
		public String suit;
		public int rank;

		public PlayingCard(String s, int r) {
			this.suit = s.toLowerCase();
			this.rank = r;
		}

		public String toString() {
			String info = "";
			if (this.rank == 1) {
				//info += "Ace";
				info += "A";
			} else if (this.rank > 10) {
				String[] cards = {"Jack", "Queen", "King"};
				//info += cards[this.rank - 11];
				info += cards[this.rank - 11].charAt(0);
			} else {
				info += this.rank;
			}
			//info += " of " + this.suit;
			info = (info + this.suit.charAt(0)).toUpperCase();
			return info;
		}

		public PlayingCard getCopy() {
			return new PlayingCard(this.suit, this.rank);   
		}

		public int getValue() {
			int i;
			for (i = 0; i < suitsInOrder.length; i++) {
				if (this.suit.equals(suitsInOrder[i]))
					break;
			}

			return this.rank + 13*i;
		}

	}

	public class Joker extends Card{
		public String redOrBlack;

		public Joker(String c) {
			if (!c.equalsIgnoreCase("red") && !c.equalsIgnoreCase("black")) 
				throw new IllegalArgumentException("Jokers can only be red or black"); 

			this.redOrBlack = c.toLowerCase();
		}

		public String toString() {
			//return this.redOrBlack + " Joker";
			return (this.redOrBlack.charAt(0) + "J").toUpperCase();
		}

		public Joker getCopy() {
			return new Joker(this.redOrBlack);
		}

		public int getValue() {
			return numOfCards - 1;
		}

		public String getColor() {
			return this.redOrBlack;
		}
	}

}
