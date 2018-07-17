package assignment1;

import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * TextMover creates a Runnable that executes a move on a text within a JPanel frame
 * 
 * @author Alexander Johansson (AF2015)
 *
 */
public class TextMover implements Runnable {
	// For toggling
	private boolean textThreadRunning = false;
	
	// JLabel for text
	private JLabel label;
	private Random rand;
	
	/**
	 * TextMover constructor
	 * @param parent JPanel to add label to
	 */
	public TextMover(JPanel parent) {
		// Create JLabel and set its dimensions and location
		label = new JLabel("Display Thread", JLabel.CENTER);
        label.setSize(100, 30);
        label.setLocation(5, 5);
        // Add to parent frame
		parent.add(label);
		
		// Initialise Random object
		rand = new Random();
	}
	
	@Override
	public void run() {	
		// While thread is running
		while(true) {
			// If toggled on, set location of label
			if(textThreadRunning) {
				// Set location within bounds (200, 200)
				label.setLocation(rand.nextInt(200 - label.getWidth()), rand.nextInt(200 - label.getHeight()));
			}
			
			// Sleep
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Method toggles execution the thread using a boolean
	 */
	public void toggleRunning() {
		textThreadRunning = !textThreadRunning;
	}
}
