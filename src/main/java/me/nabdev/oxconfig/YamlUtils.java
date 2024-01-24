package me.nabdev.oxconfig;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.extensions.MergedYamlMapping;

import static me.nabdev.oxconfig.OxConfig.config;
import static me.nabdev.oxconfig.OxConfig.configString;

/**
 * A helper class for interfacing with eo-yaml
 */
class YamlUtils {

    private static boolean dirty = false;

    /**
     * Get the YamlMapping for the given mode
     * @param mode The mode to get the mapping for
     * @return The mapping for the given mode
     */
    static YamlMapping getModeMap(String mode) {
        return config.yamlMapping(mode);
    }

    /**
     * Ensure the mode key exists
     * @param mode The default mode to use if the key does not exist
     */
    static void ensureModeExists(String mode) {
        if(config.string("mode") == null){
            Logger.logInfo("Mode key does not exist, creating");
            config = new MergedYamlMapping(config, Yaml.createYamlMappingBuilder().add("mode", mode).build(), true);
            configString = config.toString();
        }
    }

    /**
     * Change the current mode
     * @param mode The mode to change to
     */
    static void modifyMode(String mode) {
        config = new MergedYamlMapping(config, Yaml.createYamlMappingBuilder().add("mode", mode).build(), true);
        configString = config.toString();
    }

    /**
     * Change the value of a key
     * @param mode the mode to change the value for
     * @param key The key to change
     * @param newValue The new value
     * @param comment The comment to add to the key
     */
    static void modifyValue(String mode, String key, String newValue, String comment) {
        editMap(true, mode, key, newValue, comment);
    }

    /**
     * Ensures that the given key exists, and if not, adds it with the given default value.
     * @param mode The mode to ensure the key exists in
     * @param key The key to ensure exists
     * @param defaultVal The default value to use if the key does not exist
     */
    static void ensureExists(String mode, String key, String defaultVal) {
        editMap(false, mode, key, defaultVal, "Auto-Generated");
    }

    /**
     * Updates the config string to make sure its up to date
     */
    static void updateConfigStr(){
        if(!dirty){
            return;
        }
        dirty = false;
        Logger.logInfo("Updating config string");
        OxConfig.configString = config.toString();
    }
    /**
     * For internal use only
     * @param force Whether to force the key to be changed (toggle between ensure and modify)
     * @param key The key to ensure exists
     * @param defaultVal The default value to use if the key does not exist
     * @param comment The comment to add to the key
     */
    private static void editMap(boolean force, String mode, String key, String defaultVal, String comment) {
        // If the key already exists, return the original mapping
        YamlMapping nested = getModeMap(mode);
        if(!force && nested != null && nested.string(key) != null){
            return;
        }

        YamlMappingBuilder newMap = Yaml.createYamlMappingBuilder();
        newMap = newMap.add(key, Yaml.createYamlScalarBuilder()
                .addLine(defaultVal)
                .buildPlainScalar(comment));
        config = new MergedYamlMapping(config, Yaml.createYamlMappingBuilder().add(mode, newMap.build()).build(), true);
    
        dirty = true;
        OxConfig.hasModified = true;
        OxConfig.pendingNTUpdate = true;
    }
}
