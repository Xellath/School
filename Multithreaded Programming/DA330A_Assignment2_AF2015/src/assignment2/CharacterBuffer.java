package assignment2;

import javax.swing.JTextArea;

/**
 * CharacterBuffer provides us with a way to write and read a character, both synchronously and asynchronously
 * 
 * @author Alexander Johansson (AF2015)
 *
 */
public class CharacterBuffer {
	// Buffer for character
	private char charBuffer = Character.MIN_VALUE;
	
	// Element to append text to in GUI
	private JTextArea listW;
	private JTextArea listR;

	/**
	 * Constructor
	 * 
	 * @param listW JTextArea Writer text area
	 * @param listR JTextArea Reader text area
	 */
	public CharacterBuffer(JTextArea listW, JTextArea listR) {
		this.listW = listW;
		this.listR = listR;
	}

	/**
	 * Method sets buffer to input parameter, if there is a char (NOT MIN_VALUE) it waits until notified, thus giving us synchronization
	 * @param ch char input character
	 */
	public synchronized void putSynchronized(char ch) {
		// Wait
		while (charBuffer != Character.MIN_VALUE) {
			try {
				wait();
			} catch (InterruptedException e) {
			} finally {
			}
		}
		
		// Set char and append to UI element
		charBuffer = ch;
		
		listW.append("Writing " + ch + "\n");

		// Notify threads
		notify();
	}

	/**
	 * Method returns value of buffer, if there is no char (MIN_VALUE) it waits until notified, thus giving us synchronization
	 * @return char
	 */
	public synchronized char getSynchronized() {
		// Wait
		while (charBuffer == Character.MIN_VALUE) {
			try {
				wait();
			} catch (InterruptedException e) {
			} finally {
			}
		}
		
		// Store in temp, append to UI element
		char ch = charBuffer;

		listR.append("Reading " + ch + "\n");

		// Notify and set to MIN_VALUE
		notify();
		charBuffer = Character.MIN_VALUE;
		return ch;
	}
	
	/**
	 * Method sets buffer to input parameter char, asynchronously
	 * @param ch
	 */
	public void putAsynchronized(char ch) {
		// Set buffer and appent to UI element
		charBuffer = ch;
		
		listW.append("Writing " + ch + "\n");
	}

	/**
	 * Method returns character from buffer, asynchronously
	 * @return
	 */
	public char getAsynchronized() {
		// Return char and append to UI element
		char ch = charBuffer;

		listR.append("Reading " + ch + "\n");
		
		return ch;
	}
}