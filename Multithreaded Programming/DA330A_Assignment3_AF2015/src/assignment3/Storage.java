package assignment3;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;

import javax.swing.JProgressBar;

/**
 * Storage is a queue that holds FoodItems produced by Factory.
 * 
 * @author Alexander Johansson (AF2015)
 *
 */
public class Storage {
	// LinkedList to store queue
	private LinkedList<FoodItem> storage = new LinkedList<FoodItem>();
	
	// Semaphores: capacity, capacity filled and mutex
	private Semaphore capacity;
	private Semaphore capacityFilled;
	private Semaphore mutex;
	
	// UI element
	private JProgressBar status;
	
	/**
	 * Storage Constructor
	 * @param capacity int
	 * @param status JProgressBar
	 */
	public Storage(int capacity, JProgressBar status) {
		this.capacity = new Semaphore(capacity);
		this.capacityFilled = new Semaphore(0);
		this.mutex = new Semaphore(1);
		this.status = status;
	}
	
	/**
	 * Enqueue queues up a new FoodItem object at the back of the queue
	 * @param item FoodItem next queue object
	 * @throws InterruptedException 
	 */
	public void enqueue(FoodItem item) throws InterruptedException {
		capacity.acquire();
		
		mutex.acquire(); // CS
		storage.addLast(item);
		status.setValue(storage.size());
		System.out.println("Storage Size:" + storage.size()); // For debug
		mutex.release(); // CS END
		
		capacityFilled.release();
	}
	
	/**
	 * Dequeue returns queueing object from queue, and removes it from the queue
	 * @return FoodItem returns null if there is nothing in the queue
	 * @throws InterruptedException 
	 */
	public FoodItem dequeue() throws InterruptedException {
		capacityFilled.acquire();
		
		mutex.acquire(); // CS
		FoodItem item = storage.poll();
		status.setValue(storage.size());
		System.out.println("Storage Size:" + storage.size()); // For debug
		mutex.release(); // CS END
		
		capacity.release();
		return item;
	}
}
