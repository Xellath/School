package version;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * ResourceUtil handles writing to internal storage.
 *
 * @author Christoffer Nilsson
 */
public class ResourceUtil {

    private Context context;
    private JSONObject champions;
    private JSONObject masteries;
    private JSONObject runes;
    private JSONObject spells;

    /**
     * Constructor for ResourceUtil. Parameters accept application context.
     *
     * @param context application context
     */
    public ResourceUtil(Context context) {
        this.context = context;
    }

    /**
     * Writes to a specified resource file in internal storage.
     *
     * @param resourceName resource file
     * @param content      content to write
     * @throws IOException
     */
    public void writeResource(String resourceName, String content) throws IOException {
        FileOutputStream outputStream = context.openFileOutput(resourceName, Context.MODE_PRIVATE); // Open stream
        outputStream.write(content.getBytes()); // Write content
        outputStream.close();
    }

    /**
     * Returns a JSONObject from built resource originated from champions.json.
     *
     * @return <code>JSONObject</code> - JSONObject from champions.json
     */
    public JSONObject getChampions() {
        return champions;
    }

    /**
     * Returns a JSONObject from built resource originated from masteries.json.
     *
     * @return <code>JSONObject</code> - JSONObject from masteries.json
     */
    public JSONObject getMasteries() {
        return masteries;
    }

    /**
     * Returns a JSONObject from built resource originated from runes.json.
     *
     * @return <code>JSONObject</code> - JSONObject from runes.json
     */
    public JSONObject getRunes() {
        return runes;
    }

    /**
     * Returns a JSONObject from built resource originated from spells.json.
     *
     * @return <code>JSONObject</code> - JSONObject from spells.json
     */
    public JSONObject getSpells() {
        return spells;
    }

    /**
     * Builds and stores JSONObjects from resources in internal storage.
     */
    public void buildResources() {
        try {
            champions = buildResource("champions.json");
            masteries = buildResource("masteries.json");
            runes = buildResource("runes.json");
            spells = buildResource("spells.json");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds resources from internal storage.
     *
     * @param resource resource to build
     * @return <code>JSONObject</code> - JSONObject from resource
     * @throws IOException   if the URL is incorrect for the API
     * @throws JSONException if returned data is corrupt or parsed wrong
     */
    private JSONObject buildResource(String resource) throws IOException, JSONException {
        Scanner scanner = new Scanner(context.openFileInput(resource));
        String str = "";
        while (scanner.hasNext()) {
            str += scanner.nextLine();
        }
        scanner.close();
        return new JSONObject(str);
    }
}
