package cipher;

public class SolitaireCipher {
	public Deck key;

	public SolitaireCipher (Deck key) {
		this.key = new Deck(key); // deep copy of the deck
	}

	/* 
	 * TODO: Generates a keystream of the given size
	 */
	public int[] getKeystream(int size) {
		int[] keyStream = new int[size];

		for (int i = 0; i < size; i++) {
			int value = key.generateNextKeystreamValue();
			keyStream[i] = value;
		}

		return keyStream;
	}

	/* 
	 * TODO: Encodes the input message using the algorithm described in the pdf.
	 */
	public String encode(String msg) {
		String treatedMSG = "";

		for (int i = 0; i < msg.length(); i++) {
			if (Character.isAlphabetic(msg.charAt(i)))
				treatedMSG += msg.charAt(i);
		}

		msg = treatedMSG.toUpperCase();

		int size = msg.length();
		int[] keyStream = getKeystream(size);
		String encodedMSG = "";

		for (int j = 0; j < size; j++) {
			char character = (char)((msg.charAt(j) - 'A' + keyStream[j]) % 26 + 'A');
			encodedMSG += character;
		}

		return encodedMSG;
	}

	/* 
	 * TODO: Decodes the input message using the algorithm described in the pdf.
	 */
	public String decode(String msg) {
		int size = msg.length();
		int[] keyStream = getKeystream(size);
		String decodedMSG = "";

		for (int j = 0; j < size; j++) {
			char character = (char)((msg.charAt(j) - 'A' - keyStream[j] + 26) % 26 + 'A');
			decodedMSG += character;
		}

		return decodedMSG;
	}

}
