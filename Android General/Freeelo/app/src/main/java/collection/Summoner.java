package collection;

import java.io.Serializable;

/**
 * Class represents and holds information about a certain Summoner (player) from League of Legends.
 *
 * @author Christoffer Nilsson, Alexander Johansson
 */
public class Summoner implements Serializable {

    private int summonerId;
    private int wins;
    private int losses;
    private int leaguePoints;
    private String name;
    private String tier;
    private String division;
    private String masteries;
    private Spell spell1;
    private Spell spell2;
    private Champion champion;
    private RuneCollection runes;

    /**
     * Sets summoner id.
     *
     * @param summonerId id of summoner
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setSummonerId(int summonerId) {
        this.summonerId = summonerId;
        return this;
    }

    /**
     * Sets summoner name.
     *
     * @param name name of summoner
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Assigns stored Champion-object to summoner.
     *
     * @param champion Champion-object to store
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setChampion(Champion champion) {
        this.champion = champion;
        return this;
    }

    /**
     * Sets summoner spell #1.
     *
     * @param spell1 Spell-object
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setSpell1(Spell spell1) {
        this.spell1 = spell1;
        return this;
    }

    /**
     * Sets summoner spell #2.
     *
     * @param spell2 Spell-object
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setSpell2(Spell spell2) {
        this.spell2 = spell2;
        return this;
    }

    /**
     * Sets masteries for summoner.
     *
     * @param masteries masteries
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setMasteries(String masteries) {
        this.masteries = masteries;
        return this;
    }

    /**
     * Sets runes for summoner.
     *
     * @param runes runes
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setRunes(RuneCollection runes) {
        this.runes = runes;
        return this;
    }

    /**
     * Sets current amount of league points (LP).
     *
     * @param leaguePoints current amount of LP
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setLeaguePoints(int leaguePoints) {
        this.leaguePoints = leaguePoints;
        return this;
    }

    /**
     * Sets current amount of recorded losses.
     *
     * @param losses amount of losses
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setLosses(int losses) {
        this.losses = losses;
        return this;
    }

    /**
     * Sets ranked division for summoner.
     *
     * @param division ranked division
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setDivision(String division) {
        this.division = division;
        return this;
    }

    /**
     * Set ranked tier for summoner.
     *
     * @param tier ranked tier
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setTier(String tier) {
        this.tier = tier;
        return this;
    }

    /**
     * Set current amount of wins.
     *
     * @param wins amount of wins
     * @return <code>Summoner</code> - for method chain-calling
     */
    public Summoner setWins(int wins) {
        this.wins = wins;
        return this;
    }

    /**
     * Returns summoner id.
     *
     * @return <code>int</code> - summoner id
     */
    public int getSummonerId() {
        return summonerId;
    }

    /**
     * Returns summoner name.
     *
     * @return <code>String</code> - summoner name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns summoner spell #1, Spell-object.
     *
     * @return <code>Spell</code> - Spell-object
     */
    public Spell getSpell1() {
        return spell1;
    }

    /**
     * Returns summoner spell #2, Spell-object.
     *
     * @return <code>Spell</code> - Spell-object
     */
    public Spell getSpell2() {
        return spell2;
    }

    /**
     * Returns summoner champion, Champion-object.
     *
     * @return <code>Champion</code> - Champion-object
     */
    public Champion getChampion() {
        return champion;
    }

    /**
     * Returns summoner masteries.
     *
     * @return <code>String</code> - masteries
     */
    public String getMasteries() {
        return masteries;
    }

    /**
     * Returns summoner runes.
     *
     * @return <code>RuneCollection</code> - a set of rune, RuneCollection-object
     */
    public RuneCollection getRunes() {
        return runes;
    }

    /**
     * Returns amount of ranked wins for summoner.
     *
     * @return <code>int</code> - amount of wins
     */
    public int getWins() {
        return wins;
    }

    /**
     * Returns amount of league points (LP) for summoner.
     *
     * @return <code>int</code> - amount of LP
     */
    public int getLeaguePoints() {
        return leaguePoints;
    }

    /**
     * Returns amount of ranked losses for summoner.
     *
     * @return <code>int</code> - amount of losses
     */
    public int getLosses() {
        return losses;
    }

    /**
     * Returns ranked division for summoner.
     *
     * @return <code>String</code> - ranked division
     */
    public String getDivision() {
        return division;
    }

    /**
     * Returns ranked tier for summoner.
     *
     * @return code>String</code> - ranked tier
     */
    public String getTier() {
        return tier;
    }

    @Override
    public String toString() {
        return name;
    }
}