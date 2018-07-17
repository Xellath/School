package json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import java.net.URL;
import java.util.Scanner;

/**
 * Represents a JSON requester, which has methods for sending requests to the Riot API.
 *
 * @author Christoffer Nilsson, Alexander Johansson
 */
public class JSONRequester {

    private JSONParser parser;

    // TODO: API key should NOT be hard coded!
    private final String API_KEY = "api_key=8088586e-a695-4cc5-80c2-be3b6fcec3e5";
    private final String CURRENT_GAME_URL = "https://%s.api.pvp.net/observer-mode/rest/consumer/getSpectatorGameInfo/%s/%s?";
    private final String SUMMONER_ID_URL = "https://%s.api.pvp.net/api/lol/%s/v1.4/summoner/by-name/%s?";
    private final String RANKED_DATA_URL = "https://%s.api.pvp.net/api/lol/%s/v2.5/league/by-summoner/%s/entry?";
    private final String RANKED_CHAMP_DATA_URL = "https://%s.api.pvp.net/api/lol/%s/v1.3/stats/by-summoner/%s/ranked?";
    private final String PATCH_DATA_URL = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/versions?";
    private final String CHAMP_STATIC_URL = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/champion?version=%s&dataById=true&champData=image,spells&";
    private final String MASTERY_STATIC_URL = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/mastery?version=%s&masteryListData=masteryTree&";
    private final String RUNE_STATIC_URL = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/rune?version=%s&runeListData=stats&";
    private final String SPELL_STATIC_URL = "https://global.api.pvp.net/api/lol/static-data/%s/v1.2/summoner-spell?version=%s&dataById=true&spellData=cooldown,image&";

    /**
     * Constructs a new JSONRequester.
     *
     * @param parser the JSON parser to use for parsing
     */
    public JSONRequester(JSONParser parser) {
        this.parser = parser;
    }

    /**
     * Requests the current game object from the API. Returns a JSONObject containing
     * lots of information about the match such as masteries, runes etc.
     *
     * @param summonerId the ID of the summoner to search for
     * @param region     the region to search on
     * @return <code>JSONObject</code> - a JSONObject containing match information
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public JSONObject requestCurrentGameObject(int summonerId, String region) throws IOException, JSONException {
        String platformRegion = region;
        switch (region) {
            case "EUNE":
                platformRegion = "EUN1";
                break;
            case "LAN":
                platformRegion = "LA1";
                break;
            case "LAS":
                platformRegion = "LA2";
                break;
            case "OCE":
                platformRegion = "OC1";
                break;
            case "KR":
                break;
            case "RU":
                break;
            default:
                platformRegion += "1";
                break;
        }
        return buildRootObject(new URL(String.format(CURRENT_GAME_URL, region, platformRegion, summonerId) + API_KEY));
    }

    /**
     * Requests ranked champion data for the given summoner on a given region.
     *
     * @param summonerId the ID of the summoner to search for
     * @param region     the region to search on
     * @return <code>JSONArray</code> - a JSONArray containing information about wins/losses etc. for a specific champion
     * @throws IOException if the URL is incorrect for the API
     */
    public JSONArray requestRankedChampionData(int summonerId, String region) throws IOException {
        JSONArray rankedChampData;
        try {
            rankedChampData = buildRootObject(new URL(String.format(RANKED_CHAMP_DATA_URL, region, region.toLowerCase(), summonerId) + API_KEY)).
                    getJSONArray("champions");
        } catch (JSONException e) {
            return new JSONArray();
        }
        return rankedChampData;
    }

    /**
     * Requests patch data, i.e. the patch version.
     *
     * @param region the region to search on, this is not important as long as it's a valid region
     * @return <code>JSONArray</code> - a JSONArray containing various patch versions, latest to earliest
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public JSONArray requestPatchData(String region) throws IOException, JSONException {
        return buildRootArray(new URL(String.format(PATCH_DATA_URL, region) + API_KEY));
    }

    /**
     * Requests ranked data (wins/losses etc.) for a given summoner on a given region.
     *
     * @param summonerIds the ID of the summoner to search for
     * @param region      the region on which to search
     * @return <code>JSONObject</code> - a JSONObject containing ranked data
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public JSONObject requestRankedData(String summonerIds, String region) throws IOException, JSONException {
        return buildRootObject(new URL(String.format(RANKED_DATA_URL, region, region.toLowerCase(), summonerIds) + API_KEY));
    }

    /**
     * Requests static champion data (a list of all champions by ID's, not ordered).
     *
     * @param region the region to search on, this is not important as long as it's a valid region
     * @param version the version data to retrieve
     * @return <code>JSONObject</code> - a JSONObject containing all champions
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public JSONObject requestStaticChampionData(String region, String version) throws IOException, JSONException {
        return buildRootObject(new URL(String.format(CHAMP_STATIC_URL, region, version) + API_KEY));
    }

    /**
     * Requests static mastery data (a list of all masteries by ID's, not ordered).
     *
     * @param region the region to search on, this is not important as long as it's a valid region
     * @param version the version data to retrieve
     * @return <code>JSONObject</code> - a JSONObject containing all masteries
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public JSONObject requestStaticMasteryData(String region, String version) throws IOException, JSONException {
        return buildRootObject(new URL(String.format(MASTERY_STATIC_URL, region, version) + API_KEY));
    }

    /**
     * Requests static rune data (a list of all runes by ID's, not ordered).
     *
     * @param region the region to search on, this is not important as long as it's a valid region
     * @param version the version data to retrieve
     * @return <code>JSONObject</code> - a JSONObject containing all runes
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public JSONObject requestStaticRuneData(String region, String version) throws IOException, JSONException {
        return buildRootObject(new URL(String.format(RUNE_STATIC_URL, region, version) + API_KEY));
    }

    /**
     * Requests static summoner spell data (a list of all spells by ID's, not ordered).
     *
     * @param region the region to search on, this is not important as long as it's a valid region
     * @param version the version data to retrieve
     * @return <code>JSONObject</code> - a JSONObject containing all spells
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public JSONObject requestStaticSpellData(String region, String version) throws IOException, JSONException {
        return buildRootObject(new URL(String.format(SPELL_STATIC_URL, region, version) + API_KEY));
    }

    /**
     * Requests a JSONObject containing summoner ID.
     *
     * @param summonerName the name of the summoner
     * @param region       the region on which the summoner is located
     * @return <code>JSONObject</code> - a JSONObject containing summoner ID
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public JSONObject requestSummonerObject(String summonerName, String region) throws IOException, JSONException {
        String urlSummonerName = summonerName.replace(" ", "%20"); // Formats the Summoner Name to work in a URL context (spaces = %20).
        JSONObject root = buildRootObject(new URL(String.format(SUMMONER_ID_URL, region, region, urlSummonerName) + API_KEY));
        return (JSONObject) parser.parse(root, summonerName.replace(" ", "").toLowerCase()); // In the JSON object, the name is lower case and without spaces.
    }

    /**
     * Opens a stream to the API server and reads the response using a Scanner.
     *
     * @param url the URL to open stream to
     * @return <code>JSONObject</code>
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    private JSONObject buildRootObject(URL url) throws IOException, JSONException {
        Scanner scanner = new Scanner(url.openStream());
        String str = "";
        while (scanner.hasNext()) {
            str += scanner.nextLine();
        }
        scanner.close();
        return new JSONObject(str);
    }

    /**
     * Opens a stream to the API server and reads the response using a Scanner.
     *
     * @param url the URL to open stream to
     * @return <code>JSONArray</code>
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    private JSONArray buildRootArray(URL url) throws IOException, JSONException {
        Scanner scanner = new Scanner(url.openStream());
        String str = "";
        while (scanner.hasNext()) {
            str += scanner.nextLine();
        }
        scanner.close();
        return new JSONArray(str);
    }
}