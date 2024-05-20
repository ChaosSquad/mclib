package net.chaossquad.mclib;

import org.bukkit.Location;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for json configurations.
 */
public final class JSONConfigUtils {

    private JSONConfigUtils() {}

    /**
     * Converts a JSONObject to a location.
     * @param json location json
     * @return location or null if invalid
     */
    public static Location jsonObjectToLocation(JSONObject json) {

        try {

            Location location = new Location(null, json.getDouble("x"), json.getDouble("y"), json.getDouble("z"));

            if (json.has("yaw")) location.setYaw(json.getFloat("yaw"));
            if (json.has("pitch")) location.setPitch(json.getFloat("pitch"));

            return location;

        } catch (JSONException e) {
            return null;
        }

    }

    /**
     * Converts a location to a JSONObject.
     * @param location location
     * @return location json
     */
    public static JSONObject locationToJSONObject(Location location) {
        JSONObject json = new JSONObject();

        json.put("x", location.getX());
        json.put("y", location.getY());
        json.put("z", location.getZ());

        json.put("yaw", location.getYaw());
        json.put("pitch", location.getPitch());

        if (location.getWorld() != null) json.put("world", location.getWorld().getName());

        return json;
    }

    /**
     * Converts a JSONArray of location json into a list of locations.
     * @param json json array of locations
     * @return list of Locations
     */
    public static List<Location> jsonLocationArrayToLocationList(JSONArray json) {
        List<Location> locations = new ArrayList<>();

        for (Object object : json) {
            if (!(object instanceof JSONObject locData)) continue;

            Location location = jsonObjectToLocation(locData);
            if (location == null) continue;

            locations.add(location);
        }

        return List.copyOf(locations);
    }

    /**
     * Converts a list of Locations into a JSONArray of location json
     * @param locations list of Locations
     * @return json array of location json
     */
    public static JSONArray locationListToJSONArray(List<Location> locations) {
        JSONArray json = new JSONArray();

        for (Location location : locations) {
            json.put(locationToJSONObject(location));
        }

        return json;
    }

}
