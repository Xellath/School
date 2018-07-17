package collection;

import java.io.Serializable;

/**
 * Represents a champion (e.g. Kayle).
 *
 * @author Alexander Johansson, Christoffer Nilsson
 */
public class Champion implements Serializable {

    private String name;
    private String title;
    private String key;
    private Ultimate ultimate;
    private int wins;
    private int losses;

    /**
     * Sets the name of the champion.
     *
     * @param name champion name
     * @return <code>Champion</code> - this object for method chaining
     */
    public Champion setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets the title of the champion.
     *
     * @param title champion title
     * @return <code>Champion</code> - this object for method chaining
     */
    public Champion setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets the key (for use in the API request URL) of the champion.
     *
     * @param key champion key
     * @return <code>Champion</code> - this object for method chaining
     */
    public Champion setKey(String key) {
        this.key = key;
        return this;
    }

    /**
     * Sets the ultimate of the champion.
     *
     * @param ultimate the champion ultimate (R) ability
     * @return <code>Champion</code> - this object for method chaining
     */
    public Champion setUltimate(Ultimate ultimate) {
        this.ultimate = ultimate;
        return this;
    }

    /**
     * Sets this champion's wins, specific to the current summoner playing it.
     *
     * @param wins the number of wins with this champion
     * @return <code>Champion</code> - this object for method chaining
     */
    public Champion setWins(int wins) {
        this.wins = wins;
        return this;
    }

    /**
     * Sets this champion's losses, specific to the current summoner playing it.
     *
     * @param losses the number of losses with this champion
     * @return <code>Champion</code> - this object for method chaining
     */
    public Champion setLosses(int losses) {
        this.losses = losses;
        return this;
    }

    /**
     * Returns this champion's name.
     *
     * @return <code>String</code> - the champion name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns this champion's title.
     *
     * @return <code>String</code> - the champion title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns this champion's key (for use in the API request URL).
     *
     * @return <code>String</code> - the champion key
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns this champion's ultimate cooldowns (3 cooldowns).
     *
     * @return <code>double[]</code> - the champion cooldowns
     */
    public double[] getUltimateCooldowns() {
        return ultimate.getCooldowns();
    }

    /**
     * Returns the ultimate image name.
     *
     * @return <code>String</code> - the ultimate image name
     */
    public String getUltimateImageName() {
        return ultimate.getImageName();
    }

    /**
     * Returns this champion's wins, specific to the current summoner playing it.
     *
     * @return <code>int</code> - number of wins
     */
    public int getWins() {
        return wins;
    }

    /**
     * Returns this champion's losses, specific to the current summoner playing it.
     *
     * @return <code>int</code> - number of losses
     */
    public int getLosses() {
        return losses;
    }

    @Override
    public String toString() {
        return name;
    }
}