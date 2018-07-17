package assignment3;

/**
 * FoodItem serves as a collection of items to be put in Storage.
 * 
 * @author Alexander Johansson (AF2015)
 *
 */
public class FoodItem {
	private String name;
	private double weight;
	private double volume;
	
	/**
	 * FoodItem Constructor
	 * 
	 * @param volume double
	 * @param weight double
	 * @param name String
	 */
	public FoodItem(double volume, double weight, String name) {
		this.volume = volume;
		this.weight = weight;
		this.name = name;
	}

	/**
	 * Method returns the name of current item
	 * @return String item name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method sets the name of current item
	 * @param name String new item name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Method returns weight of current item
	 * @return double item weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Method sets weight of current item
	 * @param weight double new item weight
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * Method returns the volume of current item
	 * @return double item volume
	 */
	public double getVolume() {
		return volume;
	}

	/**
	 * Method sets the volume of current item
	 * @param volume double new item volume
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}
}
