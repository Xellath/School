package version;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import json.JSONParser;
import json.JSONRequester;

/**
 * VersionUtil checks, parses and saves static patch data using several other classes.
 *
 * @author Christoffer Nilsson
 */
public class VersionUtil {

    private String region;
    private String latestPatch;
    private String champVersion = "";
    private String masteryVersion = "";
    private String runeVersion = "";
    private String spellVersion = "";
    private Context context;
    private JSONParser parser;
    private JSONRequester requester;
    private ResourceUtil resourceUtil;

    /**
     * Constructor for VersionUtil, accepts context and certain needed objects for it to work.
     *
     * @param context      application context
     * @param parser       JSONParser-object
     * @param requester    JSONRequester-object
     * @param resourceUtil ResourceUtil-object
     * @param region       region to check against
     */
    public VersionUtil(Context context, JSONParser parser, JSONRequester requester, ResourceUtil resourceUtil, String region) {
        this.context = context;
        this.parser = parser;
        this.requester = requester;
        this.resourceUtil = resourceUtil;
        this.region = region.toLowerCase();
    }

    /**
     * Setups resources and sets patch (champ/mastery/rune/spell) version
     */
    public void setupAndBuildResources() {
        try {
            // If not first time
            if (!isFirstTime()) {
                // Build resources from internal storage
                resourceUtil.buildResources();
                // Get static data versions
                champVersion = resourceUtil.getChampions().getString("version");
                masteryVersion = resourceUtil.getMasteries().getString("version");
                runeVersion = resourceUtil.getRunes().getString("version");
                spellVersion = resourceUtil.getSpells().getString("version");
            }
            // Set latest patch
            latestPatch = requester.requestPatchData(this.region).getString(0);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates current static data version.
     */
    public void updateVersion() {
        try {
            latestPatch = requester.requestPatchData(this.region).getString(0);
            // Request all static data one-by-one
            JSONObject champions = requester.requestStaticChampionData(region, latestPatch);
            JSONObject masteries = requester.requestStaticMasteryData(region, latestPatch);
            JSONObject runes = requester.requestStaticRuneData(region, latestPatch);
            JSONObject spells = requester.requestStaticSpellData(region, latestPatch);
            // Get versions
            champVersion = champions.getString("version");
            masteryVersion = masteries.getString("version");
            runeVersion = runes.getString("version");
            spellVersion = spells.getString("version");
            // Write as a resource to internal storage
            resourceUtil.writeResource("champions.json", champions.toString());
            resourceUtil.writeResource("masteries.json", masteries.toString());
            resourceUtil.writeResource("runes.json", runes.toString());
            resourceUtil.writeResource("spells.json", spells.toString());

            // Build and update
            resourceUtil.buildResources();
            parser.updateResources();
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if internal storage contains static data, if not, returns a true boolean.
     *
     * @return <code>boolean</code> - true if first time (has no static data), false if static data exists
     */
    public boolean isFirstTime() {
        File champions = context.getFileStreamPath("champions.json");
        File masteries = context.getFileStreamPath("masteries.json");
        File runes = context.getFileStreamPath("runes.json");
        File spells = context.getFileStreamPath("spells.json");
        if (!champions.exists() || !masteries.exists() || !runes.exists() || !spells.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Method checks if current static data is up-to-date (latest version).
     *
     * @return <code>boolean</code> - true if latest patch, false if not
     */
    public boolean isLatestVersion() {
        if (champVersion.equals(latestPatch) && masteryVersion.equals(latestPatch) &&
                runeVersion.equals(latestPatch) && spellVersion.equals(latestPatch)) {
            return true;
        }
        return false;
    }

    /**
     * Returns current patch version.
     *
     * @return <code>String</code> - current patch version, ie. "6.10"
     */
    public String getVersion() {
        return latestPatch;
    }
}