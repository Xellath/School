package collection;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import json.JSONParser;
import json.JSONRequester;

/**
 * Represents a current, active game in which 10 summoners (players) are playing.
 *
 * @author Christoffer Nilsson, Alexander Johansson
 */
public class CurrentGame {

    private JSONRequester requester;
    private JSONParser parser;
    private Summoner[] summoners = new Summoner[5];
    private String region;
    private String searchedSummoner;

    /**
     * Constructs a new CurrentGame, telling this object to use these resources
     * to find an active game. Call <code>searchCurrentGame()</code> to initialize a search for an
     * active game.
     *
     * @param searchedSummoner the summoner name that will be queried to the API
     * @param region           the region on which the summoner is
     * @param parser           the parser to utilize when parsing data from queries
     * @param requester        the requester to utilize when requesting data from the API
     */
    public CurrentGame(String searchedSummoner, String region, JSONParser parser, JSONRequester requester) {
        this.searchedSummoner = searchedSummoner;
        this.region = region;
        this.parser = parser;
        this.requester = requester;
    }

    /**
     * Searches for an active game and initializes all Summoner objects. Call
     * <code>getSummoners()</code>.
     *
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    public void searchCurrentGame() throws IOException, JSONException {
        JSONObject summonerObject = requester.requestSummonerObject(searchedSummoner, region); // summonerId is in this JSON object
        int summonerId = (int) parser.parse(summonerObject, "id"); // Needed to find currentGame
        JSONObject currentGame = requester.requestCurrentGameObject(summonerId, region); // Contains all the info about the current game
        JSONArray participants = (JSONArray) parser.parse(currentGame, "participants"); // 10 summoners are playing
        int enemyTeamId = parser.parseEnemyTeamId(participants, summonerId); // Finds the enemy team ID
        initSummoners(participants, enemyTeamId); // We need the enemy team ID because we only want enemy player info
    }

    /**
     * Creates 5 Summoner objects from a JSONArray of summoners (called participants
     * in the API). Gives each Summoner two spells, an ultimate, masteries, runes,
     * and ranked data.
     *
     * @param participants the summoners to initialize
     * @param enemyTeamId  ID of the enemy team
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    private void initSummoners(JSONArray participants, int enemyTeamId) throws IOException, JSONException {
        int index = 0;
        String summonerIds = "";
        for (int i = 0; i < 10; i++) {
            JSONObject participant = participants.getJSONObject(i);
            if (participant.getInt("teamId") == enemyTeamId) {
                String name = (String) parser.parse(participant, "summonerName");
                int championId = (int) parser.parse(participant, "championId"); // Necessary because champions are ordered by ID
                Champion champ = parser.parseChampion(championId);
                Spell spell1 = parser.parseSpell((int) parser.parse(participant, "spell1Id"));
                Spell spell2 = parser.parseSpell((int) parser.parse(participant, "spell2Id"));
                String masteries = parser.parseMasteries(participant);
                RuneCollection runes = parser.parseRuneCollection(participant);
                int summonerId = (int) parser.parse(participant, "summonerId");
                try { // This try-catch is necessary because if rankedChampData is null, player has never played ranked
                    JSONArray rankedChampData = requester.requestRankedChampionData(summonerId, region);
                    setRankedChampionData(championId, rankedChampData, champ);
                } catch (IOException e) {
                    setRankedChampionData(championId, null, champ);
                }
                // Constructs a new Summoner object using method chaining.
                summoners[index] = new Summoner().setName(name).setSpell1(spell1).
                        setSpell2(spell2).setChampion(champ).setMasteries(masteries).setRunes(runes).setSummonerId(summonerId);
                if (summonerIds.isEmpty()) { // Used to make one single request using all summonerId's at once.
                    summonerIds += summonerId;
                } else {
                    summonerIds += "," + summonerId;
                }
                index++;
            }
        }
        initRankedData(summonerIds);
    }

    /**
     * Initializes ranked data for all Summoner objects.
     *
     * @param summonerIds a String containing all summoner ID's, separated by comma
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    private void initRankedData(String summonerIds) throws IOException, JSONException {
        JSONObject rankedDataArr;
        JSONObject rankedData = null;
        rankedDataArr = requester.requestRankedData(summonerIds, region);
        for (Summoner summoner : summoners) {
            if (rankedDataArr != null) {
                try {
                    rankedData = ((JSONArray) parser.parse(rankedDataArr, Integer.toString(summoner.getSummonerId()))).getJSONObject(0);
                } catch(JSONException e) {
                    rankedData = null;
                }
            }
            setRankedData(rankedData, summoner);
        }
    }

    /**
     * Sets the ranked data such as tier, division, wins and losses for the
     * given Summoner object.
     *
     * @param rankedData the JSONObject containing all the ranked data
     * @param summoner   the summoner
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    private void setRankedData(JSONObject rankedData, Summoner summoner) throws JSONException {
        if (rankedData == null) {
            summoner.setDivision("").setLeaguePoints(0).setLosses(0).setWins(0).setTier("Provisional");
        } else {
            String tier = (String) parser.parse(rankedData, "tier");
            // Each ranked entry is for a different summoner
            JSONObject rankedEntries = ((JSONArray) parser.parse(rankedData, "entries")).getJSONObject(0);
            int leaguePoints = (int) parser.parse(rankedEntries, "leaguePoints");
            String division = (String) parser.parse(rankedEntries, "division");
            int losses = (int) parser.parse(rankedEntries, "losses");
            int wins = (int) parser.parse(rankedEntries, "wins");
            summoner.setDivision(division).setLeaguePoints(leaguePoints).setLosses(losses).setWins(wins).setTier(tier);
        }
    }

    /**
     * Sets the ranked champion data, i.e. wins and losses with a specific champion.
     *
     * @param championId      the ID of the champion of which to set the data
     * @param rankedChampData a JSONArray containing data such as wins/losses for each champion
     * @param champion        the Champion object to initialize the data to
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    private void setRankedChampionData(int championId, JSONArray rankedChampData, Champion champion) throws JSONException {
        if (rankedChampData == null) {
            champion.setWins(0).setLosses(0);
        } else {
            int wins = 0;
            int losses = 0;
            // Lots of looping here, thanks Rito
            for (int index = 0; index < rankedChampData.length(); index++) {
                // Each entry is its own JSONObject with info about the summoner's wins/losses etc.
                JSONObject entry = rankedChampData.getJSONObject(index);
                if ((int) parser.parse(entry, "id") == championId) {
                    JSONObject stats = (JSONObject) parser.parse(entry, "stats");
                    wins = (int) parser.parse(stats, "totalSessionsWon");
                    losses = (int) parser.parse(stats, "totalSessionsLost");
                }
            }
            champion.setWins(wins).setLosses(losses);
        }
    }

    /**
     * Returns all the Summoners.
     *
     * @return <code>Summoner[]</code> - an array containing all Summoner objects
     */
    public Summoner[] getSummoners() {
        return summoners;
    }

    /**
     * Returns the name of the summoner that was used to
     *
     * @return <code>String</code> - name of searched summoner
     */
    public String getSearchedSummoner() {
        return searchedSummoner;
    }
}