package me.nabdev.oxconfig;

import static me.nabdev.oxconfig.OxConfig.config;

import org.json.JSONObject;

/**
 * A helper class for interfacing with org.json
 */
class JsonUtils {

    /**
     * If we need to reconstruct the json string.
     * 
     * This is really here as a leftover from the yaml system, which was much slower
     * to create a string of, but I figure why make it slower now
     */
    static boolean dirty = false;

    /**
     * Get the JSON Mapping for the given mode
     * 
     * @param mode The mode to get the mapping for
     * @return The mapping for the given mode
     */
    static JSONObject getModeMap(String mode) {
        return getJSONObject(config, mode);
    }

    /**
     * Get the JSON Object for the given key, creating it if it does not exist
     * 
     * @param obj The object to get the key from
     * @param key The key to get
     * @return The JSON Object for the given key
     */
    static JSONObject getJSONObject(JSONObject obj, String key) {
        if (!obj.has(key)) {
            obj.put(key, new JSONObject());
        }
        return obj.getJSONObject(key);
    }

    /**
     * Get the real value of a key
     * 
     * @param map The map to get the value from
     * @param key The key to get the value for
     * @return The real value of the key
     */
    static String getRealValue(JSONObject map, String key, boolean shouldStoreComment) {
        if (key.equals("mode") || !shouldStoreComment) {
            return map.getString(key);
        }
        return getJSONObject(map, key).getString("value");
    }

    /**
     * Ensure the mode key exists
     * 
     * @param mode The default mode to use if the key does not exist
     */
    static void ensureModeExists(String mode) {
        if (!config.has("mode")) {
            Logger.logInfo("Mode key does not exist, creating");
            modifyMode(mode);
        }
    }

    /**
     * Change the current mode
     * 
     * @param mode The mode to change to
     */
    static void modifyMode(String mode) {
        config.put("mode", mode);
        dirty = true;
    }

    /**
     * Change the value of a key
     * 
     * @param mode     the mode to change the value for
     * @param key      The key to change
     * @param newValue The new value
     * @param comment  The comment to add to the key
     */
    static void modifyValue(String mode, String key, String newValue, String comment) {
        editMap(true, mode, key, newValue, comment);
    }

    /**
     * Change the value of a key
     * 
     * @param mode     the mode to change the value for
     * @param key      The key to change
     * @param newValue The new value
     * @param comment  The comment to add to the key
     */
    static void modifyValue(String mode, String key, String newValue) {
        editMap(true, mode, key, newValue);
    }

    /**
     * Ensures that the given key exists, and if not, adds it with the given default
     * value.
     * 
     * @param mode       The mode to ensure the key exists in
     * @param key        The key to ensure exists
     * @param defaultVal The default value to use if the key does not exist
     */
    static void ensureExists(String mode, String key, String defaultVal, boolean shouldStoreComment) {
        if (shouldStoreComment) {
            editMap(false, mode, key, defaultVal, "Auto-Generated");
        } else {
            editMap(false, mode, key, defaultVal);
        }

    }

    /**
     * Updates the config string to make sure its up to date
     */
    static void updateConfigStr() {
        if (!dirty) {
            return;
        }
        dirty = false;
        Logger.logInfo("Updating config string");
        if (OxConfig.prettyPrintJSON) {
            OxConfig.configString = config.toString(4);
        } else {
            OxConfig.configString = config.toString();
        }

    }

    /**
     * For internal use only
     * 
     * @param force      Whether to force the key to be changed (toggle between
     *                   ensure and modify)
     * @param key        The key to ensure exists
     * @param defaultVal The default value to use if the key does not exist
     * @param comment    The comment to add to the key
     */
    private static void editMap(boolean force, String mode, String key, String defaultVal, String comment) {
        // If the key already exists, return the original mapping
        JSONObject nested = getModeMap(mode);
        if (!force && nested != null && nested.has(key)) {
            return;
        }

        JSONObject newObject = new JSONObject();
        newObject.put("value", defaultVal);
        newObject.put("comment", comment);
        nested.put(key, newObject);

        dirty = true;
        OxConfig.hasModified = true;
        OxConfig.pendingNTUpdate = true;
    }

    /**
     * For internal use only
     * 
     * @param force      Whether to force the key to be changed (toggle between
     *                   ensure and modify)
     * @param key        The key to ensure exists
     * @param defaultVal The default value to use if the key does not exist
     */
    private static void editMap(boolean force, String mode, String key, String defaultVal) {
        // If the key already exists, return the original mapping
        JSONObject nested = getModeMap(mode);
        if (!force && nested != null && nested.has(key)) {
            return;
        }

        nested.put(key, defaultVal);

        dirty = true;
        OxConfig.hasModified = true;
        OxConfig.pendingNTUpdate = true;
    }
}
