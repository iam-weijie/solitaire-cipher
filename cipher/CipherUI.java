package cipher;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BoxLayout;

public class CipherUI extends JFrame {
    private JTextArea inputTextArea, outputTextArea;
    private JButton encodeButton, decodeButton, shuffleButton, copyButton, swapButton, copySetupButton, pasteSetupButton;
    private JTextField numOfCardsPerSuitField, numOfSuitsField, seedField, shuffleIterField;
    private SolitaireCipher cipher;
    private int width, height;

    public CipherUI() {
        setTitle("Solitaire Cipher");
        this.width = 1200;
        this.height = 600;
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initializeUI();
    }

    private void initializeUI() {    	
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        numOfCardsPerSuitField = new JTextField("13", 2);
        numOfSuitsField = new JTextField("2", 2);
        seedField = new JTextField("26022024", 6);
        shuffleIterField = new JTextField("1", 2);
        
    	JPanel copyPastePanel = new JPanel();
    	copyPastePanel.setBounds(0, 0, 120, 100);
    	copyPastePanel.setLayout(new BoxLayout(copyPastePanel, BoxLayout.Y_AXIS));

        String[] labels = {"Num of Cards/Suit:", "Num of Suits:", "Seed:", "Shuffle Iterations:"};
        JTextField[] fields = {numOfCardsPerSuitField, numOfSuitsField, seedField, shuffleIterField};

        shuffleButton = new JButton("Initialize & Shuffle");
        encodeButton = new JButton("Encode");
        decodeButton = new JButton("Decode");
        copyButton = new JButton("Copy Output to Clipboard");
        swapButton = new JButton("<-Swap->");
        copySetupButton = new JButton("Copy Params");
        pasteSetupButton = new JButton("Paste Params");
        
        controlPanel.add(copySetupButton);
//        controlPanel.add(pasteSetupButton);
        for (int i = 0; i < labels.length; i++) {
            controlPanel.add(new JLabel(labels[i]));
            controlPanel.add(fields[i]);
        }
        controlPanel.add(shuffleButton);
        

        inputTextArea = new JTextArea(10, 20);
        outputTextArea = new JTextArea(10, 20);
        new TextAreaPlaceholder("Enter encoded/decoded message here...", inputTextArea);
        new TextAreaPlaceholder("Encoded/decoded message will appear here...", outputTextArea);
        outputTextArea.setEditable(false);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(inputTextArea), new JScrollPane(outputTextArea));
        splitPane.setDividerLocation(width/2);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(encodeButton);
        buttonPanel.add(decodeButton);
        buttonPanel.add(swapButton);
        buttonPanel.add(copyButton);

        getContentPane().add(controlPanel, BorderLayout.NORTH);
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        addActionListeners();
    }

    private void addActionListeners() {
        shuffleButton.addActionListener(e -> shuffleDeck());
        encodeButton.addActionListener(e -> processText(true));
        decodeButton.addActionListener(e -> processText(false));
        copyButton.addActionListener(this::copyTextToClipboard);
        swapButton.addActionListener(this::swapInOut);
        copySetupButton.addActionListener(this::copySetup);
//        pasteSetupButton.addActionListener(this::pasteSetup);
    }

    private void shuffleDeck() {
        int numOfCardsPerSuit = Integer.parseInt(numOfCardsPerSuitField.getText());
        int numOfSuits = Integer.parseInt(numOfSuitsField.getText());
        long seed = Long.parseLong(seedField.getText());
        int shuffleCount = Integer.parseInt(shuffleIterField.getText());

        Deck deck = new Deck(numOfCardsPerSuit, numOfSuits);
        Deck.gen.setSeed(seed);
        for (int i = 0; i < shuffleCount; i++) deck.shuffle();
        cipher = new SolitaireCipher(deck);
        JOptionPane.showMessageDialog(null, "Deck initialized and shuffled " + shuffleCount + " times with seed " + seed);
    }

    private void processText(boolean isEncoding) {
        if (cipher == null) {
            JOptionPane.showMessageDialog(null, "Initialize the deck first.");
            return;
        }
        String message = inputTextArea.getText();
        String result = isEncoding ? cipher.encode(message) : cipher.decode(message);
        outputTextArea.setText(result);
    }

    private void copyTextToClipboard(ActionEvent e) {
        String textToCopy = outputTextArea.getText();
        if (textToCopy.isEmpty() || textToCopy.equals("Encoded/decoded message will appear here...")) {
            JOptionPane.showMessageDialog(null, "Nothing to copy. Write message to encode/decode.");
            return;
        }
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(textToCopy), null);
    }
    
    private void swapInOut(ActionEvent e) {
    	String outputText = outputTextArea.getText();
    	String inputText = inputTextArea.getText();
    	if (inputText.isEmpty()|| outputText.isEmpty() 
    			|| inputText.equals("Enter encoded/decoded message here...") 
    			|| outputText.equals("Encoded/decoded message will appear here...")) {
            JOptionPane.showMessageDialog(null, "Nothing to swap. Write message and encode/decode.");
            return;
        }
    	outputTextArea.setText(inputText);
    	inputTextArea.setText(outputText);
    }
    
    private void copySetup(ActionEvent e) {
    	String text = new String();
    	text += "Num of Cards: " + numOfCardsPerSuitField.getText() + ", ";
    	text += "Num of Suits: " + numOfSuitsField.getText() + ", ";
    	text += "Seed: " + seedField.getText() + ", ";
    	text += "Shuffle Iterations: " + shuffleIterField.getText();
    	
    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection(text), null);
    }
    
//    private void pasteSetup(ActionEvent e) {
//    	
//    	String params = new String();
//    	
//    	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//    	clipboard.getContents(params);
//    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CipherUI().setVisible(true));
    }

    private class TextAreaPlaceholder extends FocusAdapter {
        private final String placeholder;
        private final JTextArea textArea;

        public TextAreaPlaceholder(String placeholder, JTextArea textArea) {
            this.placeholder = placeholder;
            this.textArea = textArea;
            textArea.addFocusListener(this);
            applyPlaceholder();
        }

        private void applyPlaceholder() {
            if (textArea.getText().isEmpty() && !textArea.isFocusOwner()) {
                textArea.setText(placeholder);
                textArea.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (textArea.getText().equals(placeholder)) {
                textArea.setText("");
                textArea.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (textArea.getText().isEmpty()) {
                applyPlaceholder();
            }
        }
    }
}
