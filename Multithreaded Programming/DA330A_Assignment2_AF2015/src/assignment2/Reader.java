package assignment2;

import java.util.Random;

import javax.swing.JLabel;

/**
 * Reader accepts a CharacterBuffer, a string to read and whether to synchronize the process or not 
 * - and reads the string from the buffer using (a)synchronized methods given in CharacterBuffer
 * 
 * Reader runs as a seperate thread
 * 
 * @author Alexander Johansson (AF2015)
 *
 */
public class Reader implements Runnable {
	// Instance variables
	private CharacterBuffer characterBuffer;
	private String stringToRead;
	private boolean synchronize;
	private JLabel resultLabel;
	private Random rand;
	
	/**
	 * Constructor
	 * 
	 * @param characterBuffer CharacterBuffer
	 * @param stringToRead String
	 * @param synchronize boolean
	 * @param resultLabel JLabel
	 * @param rand Random
	 */
	public Reader(CharacterBuffer characterBuffer, String stringToRead, boolean synchronize, JLabel resultLabel, Random rand) {
		this.characterBuffer = characterBuffer;
		this.stringToRead = stringToRead;
		this.synchronize = synchronize;
		this.resultLabel = resultLabel;
		this.rand = rand;
	}
	
	@Override
	public void run() {
		// Init a new string, iterate for each character (depending on length of stringToRead)
		String readString = "";
		int currentChar = 0;
		while(currentChar < stringToRead.length()) {
			// Depending on synchronization mode, sync or async; read character from buffer
			if(synchronize) {
				readString += characterBuffer.getSynchronized();
			} else {
				readString += characterBuffer.getAsynchronized();
			}
			
			// Increment char counter, sleep for random delay
			currentChar++;
			try {
				Thread.sleep(rand.nextInt(1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		// Write result to UI element
		resultLabel.setText(readString);
	}
}
