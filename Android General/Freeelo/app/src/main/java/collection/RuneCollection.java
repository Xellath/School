package collection;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class represents a collection of Rune-objects.
 *
 * @author Christoffer Nilsson
 */
public class RuneCollection implements Serializable {

    private HashMap<String, Double> stats = new HashMap<String, Double>(); // Medium for storing all stats associated to the current stat type
    private HashMap<String, String> descriptions = new HashMap<String, String>(); // Medium for storing all descriptions associated to the current stat type
    private ArrayList<Rune> runes = new ArrayList<Rune>(); // A list of all summoner runes
    private String finalizedStats = "";

    /**
     * This method adds a rune object to the list of runes represented in RuneCollection.
     *
     * @param rune Rune-object to insert
     */
    public void add(Rune rune) {
        runes.add(rune);
    }

    /**
     * This method is called when all runes are parsed.
     */
    public void finalizeStats() {
        for (Rune rune : runes) {
            String statType = rune.getStatType(); // Get stat type
            String statType2 = rune.getStatType2(); // Get 2nd stat type if hybrid rune
            double stat = rune.getStat();
            double stat2 = rune.getStat2(); // Get 2nd stat if hybrid
            addRune(statType, stat, rune); // Add to hashmaps
            if (statType2 != null && stat2 != 0) {
                addRune(statType2, stat2, rune); // Add to hashmaps if hybrid
            }
        }
        fixDesc();
    }

    /**
     * This method adds the incoming parameters information to the medium (hashmaps) provided.
     *
     * @param statType current stat type of the current rune
     * @param stat     current stat of the current rune
     * @param rune     Rune-object
     */
    private void addRune(String statType, double stat, Rune rune) {
        if (!stats.containsKey(statType)) { // If not in hashmap, add to hashmap
            stats.put(statType, stat * rune.getCount());
            descriptions.put(statType, rune.getDescription());
        } else { // If in hashmap, store stat and remove entry, re-add after calculation
            double tempStat = stats.get(statType);
            stats.remove(statType);
            stats.put(statType, tempStat + stat * rune.getCount());
            descriptions.put(statType, rune.getDescription());
        }
    }

    /**
     * This method is called upon completion of <code>finalizeStats();</code>,
     * this method will generate a formatted string for usage through the overridden <code>toString();</code> method.
     */
    private void fixDesc() {
        Iterator it = stats.entrySet().iterator();
        boolean percent = false;
        while (it.hasNext()) { // Iterate through hashmap
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey(); // Get stat type
            double value = (double) entry.getValue(); // Get stat
            String description = descriptions.get(key); // Get description
            percent = false;
            if (key.contains("Percent")) { // Check if stat type is a PercentMod
                percent = true;
            }
            if (key.contains("Regen")) { // Check if stat type is a RegenMod
                value *= 5;
            }
            String shortDesc = ""; // cooldowns per level
            if (description.contains("(")) { // Remove the "at level 18" stuff
                shortDesc = description.substring(description.indexOf(" "), description.indexOf("(") - 1);
            } else {
                shortDesc = description.substring(description.indexOf(" "), description.length());
            }
            // The following is necessary for hybrid runes to work. This seperates the hybrid runes description into two stats
            if (key.equals("rFlatMagicPenetrationMod")) {
                shortDesc = " magic penetration";
            } else if (key.equals("rFlatArmorPenetrationMod")) {
                shortDesc = " armor penetration";
            }
            String finalStats = "";
            if (percent) { // If the desc contains the percent sign, add it to the finalStats and format, else dont add and format
                finalStats = new DecimalFormat("0.00").format(value * 100) + "%";
            } else {
                finalStats = new DecimalFormat("0.00").format(value);
            }

            if (!Double.toString(value).startsWith("-")) { // Adds a '+' if the stat doesn't begin with a '-' (which means it's positive).
                finalStats = "+" + finalStats;
            }
            finalizedStats += finalStats + shortDesc + "\n";
        }
    }

    @Override
    public String toString() {
        return finalizedStats;
    }
}