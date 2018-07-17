package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import collection.Champion;
import collection.Rune;
import collection.RuneCollection;
import collection.Spell;
import collection.Ultimate;
import version.ResourceUtil;

/**
 * Represents a JSON parser.
 *
 * @author Christoffer Nilsson.
 */
public class JSONParser {

    private JSONObject championList;
    private JSONObject masteryList;
    private JSONObject runeList;
    private JSONObject spellList;
    private ResourceUtil resourceUtil;

    /**
     * Constructs a new JSON parser for parsing JSON data.
     *
     * @param resourceUtil a resource utility to access static JSON data in internal storage
     */
    public JSONParser(ResourceUtil resourceUtil) {
        this.resourceUtil = resourceUtil;
    }

    /**
     * Parses the enemy team ID and returns it.
     *
     * @param participants a JSONArray of summoners
     * @param summonerId   the ID of the summoner which is used to determine opposite team's ID.
     * @return <code>int</code> - 100 or 200 depending on which team enemies are on
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public int parseEnemyTeamId(JSONArray participants, int summonerId) throws JSONException {
        int enemyTeamId = 0;
        for (int i = 0; i < 10; i++) { // For each summoner
            JSONObject participant = participants.getJSONObject(i);
            if (participant.getInt("summonerId") == summonerId) {
                enemyTeamId = (((int) parse(participant, "teamId")) == 100) ? 200 : 100; // Return opposite team ID
                break;
            }
        }
        return enemyTeamId;
    }

    /**
     * Parses the value associated with the given key and returns it.
     *
     * @param jsonObj the JSONObject which is being parsed
     * @param key     the key for the value
     * @return <code>Object</code> - a general Object is returned
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public Object parse(JSONObject jsonObj, String key) throws JSONException {
        return jsonObj.get(key);
    }

    /**
     * Parses masteries for the given summoner (participant). Returns a String with the
     * following format: 0/18/12, 12/0/18 etc. where the first number is ferocity, second
     * is cunning and last is resolve.
     *
     * @param participant the summoner JSON object to parse masteries for
     * @return <code>String</code> - a String representation of the masteries in the form of "0/18/12"
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public String parseMasteries(JSONObject participant) throws JSONException {
        JSONArray summonerMasteries = (JSONArray) parse(participant, "masteries");
        int ferocity = 0; // Ferocity tree
        int cunning = 0; // Cunning tree
        int resolve = 0; // Resolve tree
        for (int i = 0; i < summonerMasteries.length(); i++) {
            JSONObject summonerMastery = summonerMasteries.getJSONObject(i);
            int rank = (int) parse(summonerMastery, "rank");
            int id = (int) parse(summonerMastery, "masteryId");
            JSONObject listMastery = (JSONObject) parse(masteryList, Integer.toString(id));
            String tree = (String) parse(listMastery, "masteryTree");
            switch (tree) {
                case "Ferocity":
                    ferocity += rank;
                    break;
                case "Cunning":
                    cunning += rank;
                    break;
                case "Resolve":
                    resolve += rank;
                    break;
                default: // This literally can't happen.
                    break;
            }
        }
        return ferocity + "/" + cunning + "/" + resolve;
    }

    /**
     * Parses a summoner spell with the given spell ID. Constructs a new Spell object and
     * sets spell name, spell image name (for getting the spell image from static data),
     * and spell cooldown.
     *
     * @param spellId the ID of the spell to parse
     * @return <code>Spell</code> - a Spell object containing name, image name and cooldown
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public Spell parseSpell(int spellId) throws JSONException {
        JSONObject listSpell = (JSONObject) parse(spellList, Integer.toString(spellId));
        String spellName = (String) parse(listSpell, "name");
        JSONObject spellImageObject = (JSONObject) parse(listSpell, "image");
        String spellImageName = (String) parse(spellImageObject, "full");
        int spellCooldown = ((JSONArray) parse(listSpell, "cooldown")).getInt(0);
        return new Spell(spellId, spellName, spellImageName, spellCooldown);
    }

    /**
     * Parses the RuneCollection of the given summoner (participant). A RuneCollection is
     * simply a collection of Rune objects.
     *
     * @param participant the summoner JSON object to parse the RuneCollection for
     * @return <code>RuneCollection</code> - a RuneCollection object
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public RuneCollection parseRuneCollection(JSONObject participant) throws JSONException {
        RuneCollection runeCollection = new RuneCollection();
        JSONArray runesArray = (JSONArray) parse(participant, "runes"); // An array with JSON objects each containing "count" and "runeId".
        for (int i = 0; i < runesArray.length(); i++) { // For each JSON object (each rune type)
            Rune rune = parseRune(runesArray, i);
            runeCollection.add(rune);
        }
        runeCollection.finalizeStats();
        return runeCollection;
    }

    /**
     * Parses a single rune, and creates a new Rune object with description, the number of
     * that rune, rune stats, and stat type.
     *
     * @param runesArray the JSONArray containing all the rune types
     * @param index      which index in the runesArray to parse at
     * @return <code>Rune</code> - a Rune object
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    private Rune parseRune(JSONArray runesArray, int index) throws JSONException {
        JSONObject summonerRune = runesArray.getJSONObject(index); // The rune object from the summoner
        // Gets the rune with the same ID as summonerRune from runeList:
        JSONObject listRune = (JSONObject) parse(runeList, Integer.toString((int) parse(summonerRune, "runeId")));
        int count = (int) parse(summonerRune, "count"); // The number of that rune (max 9 runes per rune type)
        String desc = (String) parse(listRune, "description");
        JSONObject stats = (JSONObject) parse(listRune, "stats");
        JSONArray statTypes = stats.names();
        String statType = statTypes.getString(0);
        double stat = stats.getDouble(statType); // Can't use parse() because ClassCastException
        String statType2 = null;
        double stat2;
        try { // Tries to get statType 2, which exists if hybrid rune, but not otherwise
            statType2 = statTypes.getString(1);
            stat2 = stats.getDouble(statType2);
        } catch (JSONException e) {
            stat2 = 0;
        }
        return new Rune(count, statType, statType2, stat, stat2, desc);
    }

    /**
     * Parses champion data of the given champion: ultimate info, name, and title.
     *
     * @param championId the ID of the champion
     * @return <code>Champion</code> - a Champion object
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public Champion parseChampion(int championId) throws JSONException {
        JSONObject champion = (JSONObject) parse(championList, Integer.toString(championId));
        String name = (String) parse(champion, "name");
        String title = (String) parse(champion, "title");
        String key = (String) parse(champion, "key");
        Ultimate ultimate = parseUltimate(champion);
        return new Champion().setName(name).setTitle(title).setKey(key).setUltimate(ultimate);
    }

    /**
     * Parses the ultimate ability (R) from the given summoner JSONObject.
     *
     * @param champion the JSONObject to parse the ultimate from
     * @return <code>Ultimate</code> - an Ultimate object
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    private Ultimate parseUltimate(JSONObject champion) throws JSONException {
        JSONObject ultimateObject = ((JSONArray) parse(champion, "spells")).getJSONObject(3);
        String ultimateName = (String) parse(ultimateObject, "name");
        int ultimateMaxRank = (int) parse(ultimateObject, "maxrank");
        JSONArray cooldowns = (JSONArray) parse(ultimateObject, "cooldown");
        double ultimateCooldowns[] = new double[ultimateMaxRank];
        for (int i = 0; i < ultimateMaxRank; i++) {
            ultimateCooldowns[i] = cooldowns.getDouble(i);
        }
        String ultimateImageName = (String) parse(((JSONObject) parse(ultimateObject, "image")), "full");
        return new Ultimate().setName(ultimateName).setImageName(ultimateImageName).
                setCooldowns(ultimateCooldowns).setMaxRank(ultimateMaxRank);
    }

    /**
     * Updates the lists using the resource util provided in constructor.
     *
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public void updateResources() throws JSONException {
        championList = resourceUtil.getChampions().getJSONObject("data");
        masteryList = resourceUtil.getMasteries().getJSONObject("data");
        runeList = resourceUtil.getRunes().getJSONObject("data");
        spellList = resourceUtil.getSpells().getJSONObject("data");
    }
}