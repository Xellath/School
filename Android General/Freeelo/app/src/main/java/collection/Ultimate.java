package collection;

import java.io.Serializable;

/**
 * Class represents a champion ultimate spell.
 *
 * @author Christoffer Nilsson
 */
public class Ultimate implements Serializable {

    private int maxRank;
    private double[] cooldowns;
    private String name;
    private String image;

    /**
     * Sets ultimate max rank.
     *
     * @param maxRank max rank
     * @return <code>Ultimate</code> - for method chain-calling
     */
    public Ultimate setMaxRank(int maxRank) {
        this.maxRank = maxRank;
        return this;
    }

    /**
     * Sets ultimate cooldowns (per rank).
     *
     * @param cooldowns cooldowns in array format ie. [0.0, 20.0, 40.0]
     * @return <code>Champion</code> - for method chain-calling
     */
    public Ultimate setCooldowns(double[] cooldowns) {
        this.cooldowns = cooldowns;
        return this;
    }

    /**
     * Sets ultimate name.
     *
     * @param name name
     * @return <code>Ultimate</code> - for method chain-calling
     */
    public Ultimate setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Sets ultimate image (file name + file extension, ie. "MonkeyKingSpinToWin.png").
     *
     * @param image ultimate image (file name + file extension, ie. "MonkeyKingSpinToWin.png")
     * @return <code>Ultimate</code> - for method chain-calling
     */
    public Ultimate setImageName(String image) {
        this.image = image;
        return this;
    }

    /**
     * Returns ultimate max rank.
     *
     * @return <code>int</code> - max rank for ultimate
     */
    public int getMaxRank() {
        return maxRank;
    }

    /**
     * Returns cooldowns of ultimate in double array.
     *
     * @return <code>double[]</code> - array of cooldowns per rank, ie. [0.0, 20.0, 40.0]
     */
    public double[] getCooldowns() {
        return cooldowns;
    }

    /**
     * Returns ultimate name.
     *
     * @return <code>String</code> - ultimate name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns ultimate image (file name + file extension, ie. "MonkeyKingSpinToWin.png").
     *
     * @return <code>String</code> - ultimate image (file name + file extension, ie. "MonkeyKingSpinToWin.png")
     */
    public String getImageName() {
        return image;
    }
}