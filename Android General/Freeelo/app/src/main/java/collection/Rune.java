package collection;

import java.io.Serializable;

/**
 * Class represents a Rune in League of Legends.
 *
 * @author Alexander Johansson
 */
public class Rune implements Serializable {

    private int count;
    private double stat;
    private double stat2;
    private String statType; // i.e. "rFlatMagicPenetrationMod".
    private String statType2; // i.e. "rFlatMagicPenetrationMod". Only used if hybrid runes are present
    private String description;

    /**
     * Constructor for Rune-objects, takes in parameters to fully initialize a Rune-object
     *
     * @param count       number of runes of the same type
     * @param statType    current stat type of the current rune. ie. "rFlatMagicPenetrationMod".
     * @param statType2   current 2nd stat type of the current rune, only used if rune is hybrid. ie. "rFlatMagicPenetrationMod".
     * @param stat        current numerical stat of the current rune
     * @param stat2       current numberical stat of the current rune
     * @param description current description of the current rune
     */
    public Rune(int count, String statType, String statType2, double stat, double stat2, String description) {
        this.count = count;
        this.statType = statType;
        this.statType2 = statType2;
        this.stat = stat;
        this.stat2 = stat2; // If hybrid
        this.description = description;
    }

    /**
     * Returns the current stat from the current rune.
     *
     * @return <code>double</code> - current stat from the current rune
     */
    public double getStat() {
        return stat;
    }

    /**
     * Returns the second stat if it is a hybrid rune. If it isn't, this method will
     * return 0.
     *
     * @return <code>double</code> - current 2nd stat of the rune, only used if rune is hybrid
     */
    public double getStat2() {
        return stat2;
    }

    /**
     * Returns the number of runes of the same type.
     *
     * @return <code>int</code> - the number of runes of the same type
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the stat type of the current rune, ie. "rFlatMagicPenetrationMod".
     *
     * @return <code>String</code> - the stat type of the current rune
     */
    public String getStatType() {
        return statType;
    }

    /**
     * Returns the 2nd stat type of the current rune, if the rune is hybrid. If rune is not hybrid, this method returns an empty string.
     *
     * @return <code>String</code> - the 2nd stat type of the current rune
     */
    public String getStatType2() {
        return statType2;
    }

    /**
     * Returns the description of the current rune
     *
     * @return <code>String</code> - description of the current rune
     */
    public String getDescription() {
        return description;
    }
}