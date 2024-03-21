package me.nabdev.oxconfig;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONObject;
import org.json.JSONTokener;

import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.RobotBase;

/**
 * A flexible, dynamic json based automatic configuration system for FRC robots
 */
public class OxConfig {

    /**
     * Different modes for logging
     */
    public enum LoggingMode {
        /**
         * No logging
         */
        None,
        /**
         * Only errors
         */
        Errors,
        /**
         * Errors and warnings, default
         */
        Warnings,
        /**
         * Errors, warnings, and info
         */
        Info,
    }

    /**
     * Different modes for editing
     */
    public enum EditMode {
        /**
         * No editing allowed, primarily for use during competition
         */
        Locked,
        /**
         * Only editing from the config file is allowed
         */
        FileOnly,
        /**
         * Editing from the config file and NT Interface (AdvantageScope-3044) is
         * allowed, default
         */
        Unrestricted,
    }

    /**
     * Different modes for ensuring keys
     */
    public enum EnsureMode {
        /**
         * Default, Always ensures all keys exist, but is slower
         */
        Always,
        /**
         * Ensure keys only on startup. May be a little faster than AlwaysEnsure, and
         * should be pretty safe. This is default.
         */
        Startup,
        /**
         * (Not Recommended) Never ensures keys exist, do not use on the first run after
         * adding a new parameter/class.
         */
        Never,
    }

    /* Config Mapping Declarations */
    static JSONObject config;
    static String configString;
    private static final HashMap<String, Configurable<?>> configValues = new HashMap<>();
    private static final HashMap<String, ConfigurableClass> configurableClasses = new HashMap<>();
    private static final HashMap<String, ConfigurableParameter<?>> configurableParameters = new HashMap<>();

    /**
     * The current modeSelector, used to determine which config values to use
     */
    public static ModeSelector modeSelector;

    /* Internal Declarations */
    private static boolean hasReadFromFile = false;
    // Set to true when OxConfig.initialize() is called
    private static boolean initializedFromCode = false;
    // Set to true when the config system has been fully initialized
    private static boolean shouldEnsure = true;
    static boolean hasModified = false;
    static boolean hasInitialized = false;
    static boolean pendingNTUpdate = false;

    /* Internal Parameter Declarations */
    private static final String configPath = Filesystem.getDeployDirectory() + "/config.json";
    private static final int threadSleepTime = 75;
    private static EditMode editMode = EditMode.Unrestricted;
    private static EnsureMode ensureMode = EnsureMode.Startup;
    static LoggingMode loggingMode = LoggingMode.Warnings;
    static boolean isProfiling = false;
    static boolean prettyPrintJSON = true;

    /**
     * Set the edit mode of OxConfig
     * 
     * @param mode The new edit mode
     */
    public static void setEditMode(EditMode mode) {
        editMode = mode;
        Logger.logInfo("Edit mode set to " + mode.toString());
    }

    /**
     * Enable sending profiling values in ms to NetworkTables
     * (OxConfig/Profiling)
     * 
     * @param nt Whether to send profiling values to NetworkTables
     */
    public static void enableProfiling(boolean nt) {
        isProfiling = true;
        TaskTimer.nt = nt;
        Logger.logInfo("Profiling enabled");
    }

    /**
     * Enable minification of the output json file. This could save a little
     * storage or bandwidth and maybe go a little faster, but probably not much.
     * 
     */
    public static void enableMinifyJSON() {
        prettyPrintJSON = false;
    }

    /**
     * Change the default ensure behavoir of OxConfig. If you are running into
     * slowdowns or constant crashes due to missing keys, it may be worth trying
     * this.
     * 
     * @param mode The new ensure mode
     */
    public static void setEnsureMode(EnsureMode mode) {
        ensureMode = mode;
        if (mode == EnsureMode.Never)
            shouldEnsure = false;
        Logger.logInfo("Ensure mode set to " + mode.toString());
    }

    /**
     * Set the logging mode of OxConfig
     * 
     * @param mode The new logging mode
     */
    public static void setLoggingMode(LoggingMode mode) {
        loggingMode = mode;
        Logger.logInfo("Logging mode set to " + mode.toString());
    }

    /**
     * Initializes the config system, should be called in robotInit()
     */
    public static void initialize() {
        Thread name = new Thread(() -> {
            try {
                initializedFromCode = true;
                modeSelector = new ModeSelector();
                if (editMode == EditMode.Unrestricted)
                    NT4Interface.initialize();
                reload();
                Logger.logInfo("OxConfig initialized successfully");
                if (editMode != EditMode.Unrestricted)
                    return;
                while (!Thread.currentThread().isInterrupted()) {
                    runNTInterface();
                    Thread.sleep(threadSleepTime);
                }
            } catch (Exception e) {
                Logger.logError("OxConfig ran into an issue, please report this to nab138: " + e.getMessage());
                e.printStackTrace();
            }
        });
        name.setName("OxConfig Handler");
        name.setPriority(3);
        name.start();
    }

    /**
     * Sets the list of modes that OxConfig will store values for. If not called,
     * defaults to Presentation, Competition, Testing, and Simulation.
     * Simulation will be automatically added to the list of modes if not present.
     * Call before OxConfig.initialize().
     * 
     * @param modes the new list of modes
     */
    public static void setModeList(String... modes) {
        // Make sure simulation is in the list of modes and ensure all are lowercase
        ArrayList<String> modeList = new ArrayList<>();
        for (String mode : modes) {
            if (mode.equalsIgnoreCase("simulation"))
                continue;
            if (mode.contains(","))
                throw new IllegalArgumentException("Mode names cannot contain commas");
            modeList.add(mode.toLowerCase());
        }
        modeList.add("simulation");
        ModeSelector.modes = modeList.toArray(new String[0]);
    }

    /**
     * Updates all the configurable parameters/configurable classes (NOT FROM FILE,
     * use reloadFromDisk() instead)
     */
    public static void reload() {
        if (!initializedFromCode)
            return;
        TaskTimer timer = new TaskTimer();
        if (!hasReadFromFile) {
            reloadFromFile();
            hasReadFromFile = true;
        }
        timer.logTime("ReloadFile");
        reloadConfig();
        timer.logTime("ReloadConfig");
        if (hasModified) {
            writeFiles();
            hasModified = false;
        }
        timer.logTime("WriteFile");
        hasInitialized = true;
        if (ensureMode == EnsureMode.Startup)
            shouldEnsure = false;
    }

    /**
     * Reloads all values from the config file and updates the configurable
     * parameters/configurable classes
     */
    public static void reloadFromDisk() {
        if (editMode == EditMode.Locked) {
            Logger.logWarning("Attempted to reload config from disk while in locked mode, ignoring");
            return;
        }
        Logger.logInfo("Reloading config from disk");
        TaskTimer timer = new TaskTimer();
        reloadFromFile();
        timer.logTime("ReloadFile");
        reloadConfig();
        timer.logTime("ReloadConfig");
        if (hasModified) {
            writeFiles();
            hasModified = false;
        }
        timer.logTime("WriteFile");
        hasInitialized = true;
    }

    /**
     * Missing config keys will be added to the config file automatically,
     * this function will write out those autogenerated keys to a file
     */
    public static void writeFiles() {
        TaskTimer writeTimer = new TaskTimer();
        JsonUtils.updateConfigStr();
        try (FileWriter writer = new FileWriter(configPath)) {
            writer.write(configString);
            writeTimer.logTime("PrintConfig");
            Logger.logInfo("Wrote out config file successfully");
        } catch (Exception e) {
            Logger.logError(
                    "Failed to write out config file (you may need to change file permissions): " + e.getMessage());
        }
    }

    /**
     * Register a class to be automatically configured (should be called in
     * configurable class constructor).
     * This is automatically handled in ConfigurablePIDController and
     * ConfigurableSparkPIDController.
     * 
     * @param configurableClass the class to register
     */
    public static void registerConfigurableClass(ConfigurableClass configurableClass) {
        configurableClasses.put(configurableClass.getKey(), configurableClass);
        ArrayList<ConfigurableClassParam<?>> parameters = configurableClass.getParameters();
        parameters.forEach(parameter -> registerClassParameter(
                parameter.getKey(),
                parameter));
    }

    /**
     * Not for use by the user:
     * Sets up a config value to be automatically configured (Automatically handled
     * by ConfigurableParameter)
     * 
     * @param key       the json key the value will be found under in config.json
     *                  (in deploy folder)
     * @param parameter the parameter to update
     */
    public static void registerParameter(String key, ConfigurableParameter<?> parameter) {
        configValues.put(key, parameter);
        configurableParameters.put(key, parameter);
        if (hasInitialized) {
            shouldEnsure = ensureMode == EnsureMode.Never ? false : true;
            reload();
            pendingNTUpdate = true;
        }
    }

    /**
     * Not for use by the user:
     * Sets up a configurable class param automatically configured (Automatically
     * handled by ConfigurableClassParam)
     * 
     * @param key       the json key the value will be found under in config.json
     *                  (in deploy folder)
     * @param parameter the parameter to update
     */
    public static void registerClassParameter(String key, ConfigurableClassParam<?> parameter) {
        configValues.put(key, parameter);
    }

    private static void reloadFromFile() {
        try {
            // Load config json object from configPath
            FileInputStream input = new FileInputStream(configPath);
            JSONTokener tokener = new JSONTokener(input);
            config = new JSONObject(tokener);
            JsonUtils.dirty = true;
            pendingNTUpdate = true;
            Logger.logInfo("Reloaded config from file");
        } catch (Exception e) {
            Logger.logError("Failed to read config file (ensure it exists in deploy folder under config.json): "
                    + e.getMessage());
        }
    }

    private static void reloadConfig() {
        TaskTimer timer = new TaskTimer();
        for (String key : configValues.keySet()) {
            updateSingleKey(key);
            timer.logTime("ReloadSingleKey");
        }
    }

    private static void updateSingleKey(String key) {
        Configurable<?> configurable = configValues.get(key);
        String defaultVal = configurable.get().toString();

        if (key.equalsIgnoreCase("root/mode")) {
            if (shouldEnsure)
                JsonUtils.ensureModeExists(defaultVal);
            // Don't write out simulation to config file when running in sim to avoid
            // accidentally overwriting data
            if (!(RobotBase.isSimulation() && modeSelector.getMode().equals("simulation"))) {
                setValue(configurable, "mode", config);
            }
        } else {
            if (shouldEnsure) {
                for (String mode : ModeSelector.modes) {
                    JsonUtils.ensureExists(mode, key, defaultVal, configurable.shouldStoreComment());
                }
            }
            setValue(configurable, key, JsonUtils.getModeMap(modeSelector.getMode()));
        }
    }

    @SuppressWarnings("unchecked")
    private static void setValue(Configurable<?> obj, String key, JSONObject map) {
        Object value = obj.get();
        String val = JsonUtils.getRealValue(map, key, obj.shouldStoreComment());
        try {
            switch (value.getClass().getSimpleName()) {
                case "Double":
                    ((Configurable<Double>) obj).set(Double.valueOf(val));
                    break;
                case "Float":
                    ((Configurable<Float>) obj).set(Float.valueOf(val));
                    break;
                case "Integer":
                    ((Configurable<Integer>) obj).set(Integer.valueOf(val));
                    break;
                case "Boolean":
                    ((Configurable<Boolean>) obj).set(Boolean.valueOf(val));
                    break;
                case "String":
                    ((Configurable<String>) obj).set(String.valueOf(val));
                    break;
                case "Short":
                    ((Configurable<Short>) obj).set(Short.valueOf(val));
                    break;
                case "Long":
                    ((Configurable<Long>) obj).set(Long.valueOf(val));
                    break;
                default:
                    Logger.logWarning("Unknown Type for key " + key + ": " + value.getClass().getName());
                    break;
            }
        } catch (Exception e) {
            Logger.logError("Failed to set value for key" + key + ": " + e.getMessage());
        }
    }

    /**
     * Reading/writing the config over NetworkTables, used for the tuning and config
     * GUI's built in to our modified advantage scope.
     */
    private static void runNTInterface() {
        if (!NT4Interface.hasInitialized)
            return;
        if (editMode != EditMode.Unrestricted)
            return;
        TaskTimer timer = new TaskTimer();
        handleKeySetter();
        if (pendingNTUpdate) {
            pendingNTUpdate = false;
            NT4Interface.updateClasses(configurableClasses);
            timer.logTime("NT Update Classes");
            NT4Interface.updateParameters(configurableParameters);
            timer.logTime("NT Update Parameters");
            NT4Interface.updateMode();
            timer.logTime("NT Update Mode");
            JsonUtils.updateConfigStr();
            NT4Interface.updateRaw(configString);
            timer.logTime("NT Update Raw");
        }
    }

    /**
     * Read the KeySetter and ModeSetter from NT to set
     */
    private static void handleKeySetter() {
        String keySetRaw = NT4Interface.getSetKeys();
        if (!keySetRaw.isEmpty()) {
            TaskTimer timer = new TaskTimer();
            String[] keySet = keySetRaw.split(",");
            Logger.logInfo("Received NT update for key " + keySet[0]);
            for (int i = 0; i < ModeSelector.modes.length; i++) {
                String mode = ModeSelector.modes[i];
                String key = keySet[0];
                JsonUtils.modifyValue(mode, key, keySet[2 + i], keySet[1]);
            }
            updateSingleKey(keySet[0]);
            timer.logTime("NT Key Setter");
        }

        String modeSet = NT4Interface.getSetModes();
        if (Arrays.asList(ModeSelector.modes).contains(modeSet)) {
            Logger.logInfo("Mode set over NT to " + modeSet);
            JsonUtils.modifyMode(modeSet);
            reload();
            // If running in simulation, set the mode selector manually, since we overwrote
            // it before without writing out to the config file
            if (modeSelector != null && RobotBase.isSimulation() && modeSelector.getMode().equals("simulation")) {
                modeSelector.setMode(modeSet);
            }
        } else if (!modeSet.isEmpty()) {
            Logger.logWarning("Invalid mode set over NT: " + modeSet);
        }

        String classSet = NT4Interface.getSetClasses();
        if (!classSet.isEmpty()) {
            String[] keySet = classSet.split(",");
            String classSetType = keySet[0];
            Logger.logInfo("Received NT update for class " + keySet[1]);
            switch (classSetType) {
                case "single": {
                    String key = keySet[1];
                    String mode = keySet[2];
                    if (!Arrays.asList(ModeSelector.modes).contains(mode)) {
                        Logger.logWarning("Invalid mode for class set over NT: " + mode);
                        return;
                    }
                    JsonUtils.modifyValue(mode, key, keySet[3]);
                    updateSingleKey(key);
                    break;
                }
                case "copyOne": {
                    String key = keySet[1];
                    String sourceMode = keySet[2];
                    String destMode = keySet[3];

                    // Copy all values from source mode for the class key to dest mode for the class
                    // key
                    JSONObject source = JsonUtils.getModeMap(sourceMode);

                    ConfigurableClass classObj = configurableClasses.get(key);
                    if (classObj == null) {
                        Logger.logWarning("Invalid class set over NT: " + key);
                        return;
                    }

                    updateOneClass(sourceMode, source, classObj, destMode);
                    break;
                }
                case "copyAll": {
                    String key = keySet[1];
                    String sourceMode = keySet[2];

                    JSONObject source = JsonUtils.getModeMap(sourceMode);

                    ConfigurableClass classObj = configurableClasses.get(key);
                    if (classObj == null) {
                        Logger.logWarning("Invalid class set over NT: " + key);
                        return;
                    }

                    for (String mode : ModeSelector.modes) {
                        if (mode.equals(sourceMode))
                            continue;
                        updateOneClass(sourceMode, source, classObj, mode);
                    }

                    break;
                }
            }

        }
        if (hasModified) {
            TaskTimer timer = new TaskTimer();
            writeFiles();
            hasModified = false;
            timer.logTime("WriteFile");
        }

    }

    private static void updateOneClass(String sourceMode, JSONObject source, ConfigurableClass classObj, String mode) {
        for (ConfigurableClassParam<?> param : classObj.getParameters()) {
            String paramKey = param.getKey();
            if (!source.has(paramKey)) {
                continue;
            }
            String sourceVal = JsonUtils.getRealValue(source, paramKey, param.shouldStoreComment());
            JsonUtils.modifyValue(mode, paramKey, sourceVal);
            updateSingleKey(paramKey);
        }
    }

}
