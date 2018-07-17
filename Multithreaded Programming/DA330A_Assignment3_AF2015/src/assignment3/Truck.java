package assignment3;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 * Truck resembles a consumer, that consumes items stored in Storage, that were produced by Factory.
 * 
 * @author Alexander Johansson (AF2015)
 *
 */
public class Truck implements Runnable {
	// For toggling
	private boolean truckThreadRunning = false;
	
	private Storage storage;
	
	// UI elements
	private JLabel lblItems;
	private JLabel lblWeight;
	private JLabel lblVolume;
	private JLabel lblStatus;
	private JTextArea lstItemList;
	private JCheckBox chkContinueLoad;
	
	// Truck max items, weight, volume
	private int maxItems;
	private double maxWeight;
	private double maxVolume;
	
	// Truck current items, weight, volume
	private int curItems;
	private double curWeight;
	private double curVolume;
	
	/**
	 * Truck Constructor
	 * @param storage Storage
	 * @param items JLabel
	 * @param weight JLabel
	 * @param volume JLabel
	 * @param status JLabel
	 * @param itemList JTextArea
	 * @param continueLoad JCheckBox
	 */
	public Truck(Storage storage, JLabel items, JLabel weight, JLabel volume, JLabel status, JTextArea itemList, JCheckBox continueLoad) {
		this.storage = storage;
		this.lblItems = items;
		this.lblWeight = weight;
		this.lblVolume = volume;
		this.lblStatus = status;
		this.lstItemList = itemList;
		this.chkContinueLoad = continueLoad;
	}
	
	@Override
	public void run() {
		while(true) {
			// If toggled on
			if(truckThreadRunning) {
				try {
					// Retrieve item from storage
					FoodItem item = storage.dequeue();
					if(item != null) { // Item is not null
						// For debug
						System.out.println(item.getName() + " " + item.getVolume() + " " + item.getWeight());
						
						// Increment truck items, weight, volume
						curItems++;
						curWeight += item.getWeight();
						curVolume += item.getVolume();
						
						// Update UI
						lblItems.setText(Integer.toString(curItems));
						lblWeight.setText(Double.toString(curWeight));
						lblVolume.setText(Double.toString(curVolume));
						
						lstItemList.append(item.getName() + "\n");
						
						// Check if truck is fully loaded
						if(curItems == maxItems | curWeight >= maxWeight | curVolume >= maxVolume) {
							if(chkContinueLoad.isSelected()) { // Continue loading (checkbox selected)
								emptyTruck();
							} else {
								// Stand idle for a while before emptying
								lblStatus.setText("Status: Idle");
								
								// Sleep for 10 sec before emptying
								try {
									Thread.sleep(10000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								
								
								// Empty
								emptyTruck();
							}
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Sleep
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Empties truck and continues loading
	 */
	private void emptyTruck() {
		// Reset values
		lblStatus.setText("Status: Emptying");
		
		lblItems.setText("");
		lblWeight.setText("");
		lblVolume.setText("");
		
		lstItemList.setText("");
		
		curItems = 0;
		curWeight = 0.0;
		curVolume = 0.0;
		
		// Sleep 4 sec before continuing
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Set status again
		lblStatus.setText("Status: Loading");
	}
	
	/**
	 * Method toggles execution the thread using a boolean
	 */
	public void toggleRunning() {
		truckThreadRunning = !truckThreadRunning;
	}
	
	/**
	 * Sets max load for truck
	 * @param items int
	 * @param weight double
	 * @param volume double
	 */
	public void setMaxLoad(int items, double weight, double volume) {
		this.maxItems = items;
		this.maxWeight = weight;
		this.maxVolume = volume;
	}
}
