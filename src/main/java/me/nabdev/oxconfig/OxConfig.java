package me.nabdev.oxconfig;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlMappingBuilder;
import com.amihaiemil.eoyaml.YamlPrinter;
import com.amihaiemil.eoyaml.extensions.MergedYamlMapping;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;

/**
 * A flexible, dynamic YAML based automatic configuration system for FRC robots
 */
public class OxConfig {
    static YamlMapping config;
    private static final HashMap<String, Configurable<?>> configValues = new HashMap<>();
    private static final HashMap<String, ConfigurableClass> configurableClasses = new HashMap<>();
    private static final HashMap<String, ConfigurableParameter<?>> configurableParameters = new HashMap<>();

    private static boolean hasModified = false;
    private static boolean hasReadFromFile = false;
    static boolean pendingNTUpdate = false;
    static boolean hasInitialized = false;

    /**
     * The current modeSelector, used to determine which config values to use
     */
    public static ModeSelector modeSelector;
    
    private static boolean initializedFromCode = false;

    /**
     * Initializes the config system, should be called in robotInit()
     */
    public static void initialize(){
        initializedFromCode = true;
        modeSelector = new ModeSelector();
        NT4Interface.initalize();
        reload();
    }

    /**
     * Sets the list of modes that OxConfig will store values for. If not called, defaults to Presentaion, Competition, Testing, and Simulation. 
     * Simulation will be automatically added to the list of modes if not present. Call before OxConfig.initialize().
     * @param modes the new list of modes
     */
    public static void setModeList(String... modes){
        // Make sure simulation is in the list of modes and ensure all are lowercase
        ArrayList<String> modeList = new ArrayList<>();
        for(String mode : modes){
            if(mode.toLowerCase().equals("simulation")) continue;
            if(mode.contains(",")) throw new IllegalArgumentException("Mode names cannot contain commas");
            modeList.add(mode.toLowerCase());
        }
        modeList.add("simulation");
        ModeSelector.modes = modeList.toArray(new String[0]);
    }

    /**
     * Updates all the configurable parameters/configurable classes (NOT FROM FILE, use reloadFromDisk() instead)
     */
    public static void reload(){
        TaskTimer timer = new TaskTimer();
        if(!initializedFromCode) return;
        if(!hasReadFromFile){
            reloadFromFile();
            hasReadFromFile = true;
        }
        timer.logTime("ReloadFile");
        reloadConfig();
        timer.logTime("ReloadConfig");
        if(hasModified){
            writeFiles();
            hasModified = false;
        }
        timer.logTime("WriteFile");
        hasInitialized = true;
    }

    /**
     * Reloads all values from the config file and updates the configurable parameters/configurable classes
     */
    public static void reloadFromDisk(){
        reloadFromFile();
        reloadConfig();
        if(hasModified){
            writeFiles();
            hasModified = false;
        }
        hasInitialized = true;
    }

    /**
     * Missing config keys will be added to the config file automatically,
     * this function will write out those autogenerated keys to a file
     */
    public static void writeFiles(){
        try {
            final YamlPrinter printer = Yaml.createYamlPrinter(
                new FileWriter(Filesystem.getDeployDirectory() + "/config.yml")
            );
            printer.print(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Register a class to be automatically configured (should be called in configurable class constructor).
     * This is automatically handled in ConfigurablePIDController and ConfigurableSparkPIDController.
     * @param configurableClass the class to register
     */
    public static void registerConfigurableClass(ConfigurableClass configurableClass){
        configurableClasses.put(configurableClass.getKey(), configurableClass);
        ArrayList<ConfigurableClassParam<?>> parameters = configurableClass.getParameters();
        parameters.forEach(parameter -> {
            registerClassParameter(
                parameter.getKey(),
                parameter
            );
        });
    }

    /**
     * Not for use by the user: 
     * Sets up a config value to be automatically configured (Automatically handled by ConfigurableParameter)
     * @param key the yaml key the value will be found under in config.yml (in deploy folder)
     * @param parameter the parameter to update
     */
    public static void registerParameter(String key, ConfigurableParameter<?> parameter){
        configValues.put(key, parameter);
        configurableParameters.put(key, parameter);
        //reload();
    }

    /**
     * Not for use by the user:
     * Sets up a configurable class param  automatically configured (Automatically handled by ConfigurableClassParam)
     * @param key the yaml key the value will be found under in config.yml (in deploy folder)
     * @param parameter the parameter to update
     */
    public static void registerClassParameter(String key, ConfigurableClassParam<?> parameter){
        configValues.put(key, parameter);
        //reload();
    }

    private static void reloadFromFile(){
        try {
            config = Yaml.createYamlInput(new File(Filesystem.getDeployDirectory() + "/config.yml")).readYamlMapping();
            pendingNTUpdate = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void reloadConfig(){
        for(String key : configValues.keySet()){
            String[] keys = key.split("/");
            if(keys[0].equalsIgnoreCase("root")){
                String newKey = String.join("/", Arrays.copyOfRange(keys, 1, keys.length));
                ensureExists(newKey, configValues.get(key).get().toString());
                if(key == "root/mode"){
                    if(!(RobotBase.isSimulation() && modeSelector.getMode().equals("simulation"))){
                        setValue(configValues.get(key), keys[keys.length - 1], getNestedValue(newKey, config), key);
                    }
                } else {
                    setValue(configValues.get(key), keys[keys.length - 1], getNestedValue(newKey, config), key);
                }
            } else {
                for(String mode : ModeSelector.modes) {
                    ensureExists(mode + "/" + key, configValues.get(key).get().toString());
                }
                setValue(configValues.get(key), keys[keys.length - 1], getNestedValue(modeSelector.getMode() + "/" + key, config), key);
            }
        }
    }
    @SuppressWarnings("unchecked")
    private static void setValue(Configurable<?> obj, String key, YamlMapping map, String fullKey){
        try {
            if(obj.get() instanceof Double){
                ((Configurable<Double>)obj).set(map.doubleNumber(key));
            } else if (obj.get() instanceof Float){
                ((Configurable<Float>)obj).set(map.floatNumber(key));
            } else if(obj.get() instanceof Integer){
                ((Configurable<Integer>)obj).set(map.integer(key));
            } else if(obj.get() instanceof Boolean){
                ((Configurable<Boolean>)obj).set(Boolean.valueOf(map.string(key)));
            } else if(obj.get() instanceof String){
                ((Configurable<String>)obj).set(String.valueOf(map.string(key)));
            } else if(obj.get() instanceof Short){
                ((Configurable<Short>)obj).set(Short.valueOf(map.string(key)));
            } else if(obj.get() instanceof Long){
                ((Configurable<Long>)obj).set(map.longNumber(key));
            } 
            else {
                System.out.println("Unknown OxConfig type: " + obj.get().getClass().getName());
            }
        } catch (Exception e) {
            System.out.println("Error setting config value: " + fullKey);
            e.printStackTrace();
        }
    }

    /**
     * Ensures that the given key exists in the given YamlMapping, and if it does not, adds it with the given default value. 
     * Will not overwrite any existing values, even in higher levels of the heirarchy
     * @param key The key to ensure exists, in the form of "key1/key2/key3"
     * @param defaultVal The default value to use if the key does not exist
     */
    private static void ensureExists(String key, String defaultVal) {
        String[] keys = key.split("/");
        // If the key already exists, return the original mapping
        YamlMapping nested = getNestedValue(key, config);
        if(nested != null && nested.string(keys[keys.length - 1]) != null){
            return;
        }
        hasModified = true;
        pendingNTUpdate = true;

        YamlMappingBuilder newMap = Yaml.createYamlMappingBuilder();
        newMap = newMap.add(keys[keys.length - 1], Yaml.createYamlScalarBuilder()
            .addLine(defaultVal)
            .buildPlainScalar(
                "Auto-generated"
            ));
            
        // Iterate backwards through the keys, creating the heirarchy from the bottom up
        for(int i = keys.length - 2; i >= 0; i--){
            newMap = Yaml.createYamlMappingBuilder().add(keys[i], newMap.build());
        }
        config = new MergedYamlMapping(config, newMap.build(), true);
    }

    /**
     * Change the value of a key
     * @param key The key to change, in the form of "key1/key2/key3"
     * @param newValue The new value
     * @param source The YamlMapping to check
     * @return A new YamlMapping that is guaranteed to have the given key
     */
    private static YamlMapping modifyValue(String key, String newValue, String comment, final YamlMapping source) {
        String[] keys = key.split("/");
        hasModified = true;
        pendingNTUpdate = true;

        YamlMappingBuilder newMap = Yaml.createYamlMappingBuilder();
        newMap = newMap.add(keys[keys.length - 1], Yaml.createYamlScalarBuilder()
            .addLine(newValue)
            .buildPlainScalar(comment)
        );

        // Iterate backwards through the keys, creating the hierarchy from the bottom up
        for(int i = keys.length - 2; i >= 0; i--){
            newMap = Yaml.createYamlMappingBuilder().add(keys[i], newMap.build());
        }
        YamlMapping test = new MergedYamlMapping(source, newMap.build(), true);
        return test;
    }

    /**
     * Returns the mapping that is one step up in the hierarchy from the given key
     * @param key The key to get, in the form of "key1/key2/key3"
     * @param source The YamlMapping to get the key from
     * @return The nested YamlMapping, null if not found
     */
    static YamlMapping getNestedValue(String key, YamlMapping source){
        String[] keys = key.split("/");
        YamlMapping map = source;
        for(int i = 0; i < keys.length - 1; i++){
            if(map == null) return map;
            map = map.yamlMapping(keys[i]);
        }
        return map;
    }


    /**
     * Reading/writing the config over NetworkTables, used for the tuning and config GUI's built in to our modified advantage scope.
     * OxConfig can be run without this if you aren't interested in these features. Designed to be run in periodic.
     */
    public static void runNTInterface(){
        if(!NT4Interface.hasInitialized) return;
        handleKeySetter();
        if(pendingNTUpdate){
            pendingNTUpdate = false;
            NT4Interface.updateClasses(configurableClasses);
            NT4Interface.updateParameters(configurableParameters);
            NT4Interface.updateMode();
            NT4Interface.updateRaw(config);
        }
    }

    /**
     * Read the KeySetter and ModeSetter from NT to set
     */
    private static void handleKeySetter(){
        String keySetRaw = NT4Interface.getSetKeys();
        if(!keySetRaw.isEmpty()){
            String[] keySet = keySetRaw.split(",");
            for(int i = 0; i < ModeSelector.modes.length; i++){
                String mode = ModeSelector.modes[i];
                String key = keySet[0];
                if(!key.split(",")[0].equals("root")){
                    key = mode + "/" + keySet[0];
                }
                config = modifyValue(key, keySet[2 + i], keySet[1], config);
            }
            reload();
        }

        String modeSet = NT4Interface.getSetModes();
        if(Arrays.asList(ModeSelector.modes).contains(modeSet)){
            config = modifyValue("mode", modeSet, "Current Mode", config);
            reload();
            if(modeSelector != null && RobotBase.isSimulation() && modeSelector.getMode().equals("simulation")){
                modeSelector.setMode(modeSet);
            }
        }

        String classSet = NT4Interface.getSetClasses();
        if(!classSet.isEmpty()){
            String[] keySet = classSet.split(",");
            String classSetType = keySet[0];
            if(classSetType.equals("single")){
                String key = keySet[1];
                String mode = keySet[2];
                if(!Arrays.asList(ModeSelector.modes).contains(mode)){
                    System.out.println("Invalid mode: " + mode);
                    return;
                }
                config = modifyValue(mode + "/" + key, keySet[3], "Modified by Tuner", config);
            } else if(classSetType.equals("copyOne")){
                String key = keySet[1];
                String sourceMode = keySet[2];
                String destMode = keySet[3];

                // Copy all values from source mode for the class key to dest mode for the class key
                YamlMapping source = config.yamlMapping(sourceMode);
                
                ConfigurableClass classObj = configurableClasses.get(key);
                if(classObj == null){
                    System.out.println("Invalid class: " + key);
                    return;
                }

                for(ConfigurableClassParam<?> param : classObj.getParameters()){
                    String paramKey = param.getKey();
                    String[] paramKeys = paramKey.split("/");
                    YamlMapping nested = getNestedValue(paramKey, source);
                    if(nested.string(paramKeys[paramKeys.length - 1]) != null){
                        config = modifyValue(destMode + "/" + paramKey, nested.string(paramKeys[paramKeys.length - 1]), ("Modified by Tuner (Copied from " + sourceMode + ")"), config);
                    }
                }
            } else if(classSetType.equals("copyAll")){
                String key = keySet[1];
                String sourceMode = keySet[2];

                // Copy all values from source mode for the class key to dest mode for the class key
                YamlMapping source = config.yamlMapping(sourceMode);
                
                ConfigurableClass classObj = configurableClasses.get(key);
                if(classObj == null){
                    System.out.println("Invalid class: " + key);
                    return;
                }

                for(String mode : Arrays.asList(ModeSelector.modes)){
                    if(mode == sourceMode) continue;
                    for(ConfigurableClassParam<?> param : classObj.getParameters()){
                        String paramKey = param.getKey();
                        String[] paramKeys = paramKey.split("/");
                        YamlMapping nested = getNestedValue(paramKey, source);
                        if(nested.string(paramKeys[paramKeys.length - 1]) != null){
                            config = modifyValue(mode + "/" + paramKey, nested.string(paramKeys[paramKeys.length - 1]), ("Modified by Tuner (Copied from " + sourceMode + ")"), config);
                        }
                    }
                }
                
            }
            
            reload();
        }
        
    }

    static String appendModeIfNotRoot(String key){
        String[] keys = key.split("/");
        if(keys[0].equalsIgnoreCase("root")){
            return String.join("/", Arrays.copyOfRange(keys, 1, keys.length));
        } else {
            return modeSelector.getMode() + "/" + key;
        }
    }
}
 