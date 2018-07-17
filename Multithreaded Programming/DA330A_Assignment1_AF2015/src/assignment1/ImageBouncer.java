package assignment1;

import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * ImageBouncer creates a Runnable that executes a move on an image within a JPanel frame
 * 
 * @author Alexander Johansson (AF2015)
 *
 */
public class ImageBouncer implements Runnable {
	private boolean textThreadRunning = false;
	
	/*
	 * Label, x,y set to 100,100 (which is middle of JPanel)
	 */
	private JPanel parent;
	private JLabel label;
	private int labelX = 100;
	private int labelY = 100;
	
	private int nextX;
	private int nextY;
	
	private Random rand;
	
	/**
	 * ImageBouncer constructor
	 * @param parent JPanel to add label to
	 */
	public ImageBouncer(JPanel parent) {
		this.parent = parent;
		
		// Create label, set its dimensions and location
		label = new JLabel(new ImageIcon("files/image.png"));
        label.setSize(50, 50);
        label.setLocation(labelX, labelY);
        // Add to parent
		parent.add(label);
		
		// Initialise Random object
		rand = new Random();
		
		// Set randomized initial speed
		nextX = rand.nextInt(5) + 2;
		nextY = rand.nextInt(5) + 2;
	}
	
	@Override
	public void run() {	
		// While thread is running
		while(true) {
			// If toggled on
			if(textThreadRunning) {
				// Set random speeds and directions
				labelX += nextX;
                labelY += nextY;
                // If above width, set back to within bounds, and reverse direction
                if (labelX + label.getWidth() > parent.getWidth()) {
                    labelX = parent.getWidth() - label.getWidth();
                    nextX = -(rand.nextInt(5) + 2);
                } else if (labelX < 0) { // If below 0, set inside bounds (0), and new direction
                    labelX = 0;
                    nextX = rand.nextInt(5) + 2;
                }
                
                // If above height, set back to within bounds, and reverse direction
                if (labelY + label.getHeight() > parent.getHeight()) {
                    labelY = parent.getHeight() - label.getHeight();
                    nextY = -(rand.nextInt(5) + 2);
                } else if (labelY < 0) { // If below 0, set inside bounds (0), and new direction
                    labelY = 0;
                    nextY = rand.nextInt(5) + 2;
                }
				
				label.setLocation(labelX, labelY);
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
