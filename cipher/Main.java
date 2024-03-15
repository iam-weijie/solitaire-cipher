package cipher;
import java.util.Random;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int minInt = Integer.MIN_VALUE;
        int maxInt = Integer.MAX_VALUE;

        int option = getIntInput(scanner, "Enter 1 to encode and 2 to decode: ", 1, 2);
        int seed = getIntInput(scanner, "Enter a seed: ", minInt, maxInt);
        int numOfCardsPerSuit = getIntInput(scanner, "Enter a number of cards per suit between 1 and 13: ", 1, 13);
        int numOfSuits = getIntInput(scanner, "Enter a number of suits between 1 and 4: ", 1, 4);
        String message = getMessage(scanner);

        scanner.close();

        Deck deck = new Deck(numOfCardsPerSuit, numOfSuits);
        Deck.gen = new Random(seed);
        deck.shuffle();

        System.out.println("----------");
        SolitaireCipher cipher = new SolitaireCipher(deck);

        if (option == 1) {
            System.out.println("Your encoded message is: " + cipher.encode(message));
            System.out.println("Your seed is: " + seed);
            System.out.println("The deck contains " + numOfCardsPerSuit + " cards per suit and " + numOfSuits + " suits.");
        } else if (option == 2) {
            System.out.println("Your decoded message is: " + cipher.decode(message));
        }

        System.out.println("----------");   
    }

    private static int getIntInput(Scanner scanner, String prompt, int min, int max) {
        int input = 0;
        boolean valid = false;
        while (!valid) {
            System.out.print(prompt);
            try {
                input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    valid = true;
                    return input;
                } else {
                    System.out.println("Invalid input. Please enter an integer between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
        return -1;
    }

    private static String getMessage(Scanner scanner) {
        System.out.println("Enter a message:");
        return scanner.nextLine();
    }

}
