package assignment3;

import java.util.Random;

/**
 * Factory is resembles a producer, it produces items for the Storage.
 * 
 * @author Alexander Johansson (AF2015)
 *
 */
public class Factory implements Runnable {
	// For toggling
	private boolean factoryThreadRunning = false;
	
	// Storage and FoodBuffer for usage in thread
	private Storage storage;
	private FoodItem[] foodBuffer;
	private Random rand;

	/**
	 * Factory Constructor
	 * @param storage Storage
	 * @param foodBuffer FoodItem[]
	 * @param rand Random
	 */
	public Factory(Storage storage, FoodItem[] foodBuffer, Random rand) {
		this.storage = storage;
		this.foodBuffer = foodBuffer;
		this.rand = rand;
	}
	
	@Override
	public void run() {
		while(true) {
			// If toggled on
			if(factoryThreadRunning) {
				try {
					// Queue random item
					storage.enqueue(foodBuffer[rand.nextInt(20)]);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Sleep
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Method toggles execution the thread using a boolean
	 */
	public void toggleRunning() {
		factoryThreadRunning = !factoryThreadRunning;
	}
}
