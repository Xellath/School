package se.mah.af2015.worldandfriends;

import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.StringWriter;

public class JsonHandler {
    private MainActivity mActivity;

    public JsonHandler(MainActivity activity) {
        mActivity = activity;
    }

    public void getMemberLocations(JSONObject jsonObject, String group, MapFragment mapsFragment) {
        try {
            mapsFragment.clearMapMarkers();
            if (jsonObject.get("type").equals("locations") && jsonObject.get("group").equals(group)) {
                JSONArray jsonArray = jsonObject.getJSONArray("location");
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject locationsJSONObject = jsonArray.getJSONObject(i);
                        Log.d("MemberLocations", "" + locationsJSONObject.getDouble("latitude") + " " + locationsJSONObject.getDouble("longitude"));
                        mapsFragment.addMapMarker(locationsJSONObject.getDouble("longitude"), locationsJSONObject.getDouble("latitude"), locationsJSONObject.getString("member"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getGroups(JSONObject jsonObject) {
        try {
            mActivity.clearGroupArray();
            if (jsonObject.get("type").equals("groups")) {
                JSONArray jsonArray = jsonObject.getJSONArray("groups");
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject groupJSONObject = jsonArray.getJSONObject(i);
                        mActivity.addToGroupArray(groupJSONObject.get("group").toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                MainActivity.mGroupFragment.updateAdapter();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String register(String group, String name) {
        return jsonFormatter(new String[][]{
                {"type", "register"},
                {"group", group},
                {"member", name}
        });
    }

    public String unregister(String id) {
        return jsonFormatter(new String[][]{
                {"type", "unregister"},
                {"id", id}
        });
    }

    public String groups() {
        return jsonFormatter(new String[][]{
                {"type", "groups"}
        });
    }

    public String location(String id, String lat, String lng) {
        return jsonFormatter(new String[][]{
                {"type", "location"},
                {"id", id},
                {"longitude", lng},
                {"latitude", lat}
        });
    }

    private String jsonFormatter(String[][] nameValues) {
        String jsonQuery = "";
        try {
            StringWriter stringWriter = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(stringWriter);
            jsonWriter.beginObject();
            for (String[] nameValue : nameValues) {
                jsonWriter.name(nameValue[0]).value(nameValue[1]);
            }
            jsonWriter.endObject();
            jsonQuery = stringWriter.toString();
            jsonWriter.close();
            stringWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonQuery;
    }
}
