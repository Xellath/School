package assignment2;

import java.util.Random;

import javax.swing.JLabel;

/**
 * Writer accepts a CharacterBuffer, a string to write and whether to synchronize the process or not 
 * - and writes the string as characters using (a)synchronized methods given in CharacterBuffer
 * 
 * Writer runs as a seperate thread
 * 
 * @author Alexander Johansson (AF2015)
 *
 */
public class Writer implements Runnable {
	// Instance variables
	private CharacterBuffer characterBuffer;
	private String stringToWrite;
	private boolean synchronize;
	private JLabel resultLabel;
	private Random rand;
	
	/**
	 * Constructor
	 * 
	 * @param characterBuffer CharacterBuffer 
	 * @param stringToWrite String
	 * @param synchronize boolean
	 * @param resultLabel JLabel
	 * @param rand Random
	 */
	public Writer(CharacterBuffer characterBuffer, String stringToWrite, boolean synchronize, JLabel resultLabel, Random rand) {
		this.characterBuffer = characterBuffer;
		this.stringToWrite = stringToWrite;
		this.synchronize = synchronize;
		this.resultLabel = resultLabel;
		this.rand = rand;
	}
	
	@Override
	public void run() {
		// Iterate through string
		int currentChar = 0;
		while(currentChar < stringToWrite.length()) {
			// Depending on synchronization mode, sync or async; write current character from index position in string
			if(synchronize) {
				characterBuffer.putSynchronized(stringToWrite.charAt(currentChar));
			} else {
				characterBuffer.putAsynchronized(stringToWrite.charAt(currentChar));
			}
			
			// Increment index position, sleep for random delay
			currentChar++;
			try {
				Thread.sleep(rand.nextInt(1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Write result to UI element
		resultLabel.setText(stringToWrite);
	}
}
