package me.nabdev.oxconfig;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * A helper class for interfacing with NetworkTables
 */
class NT4Interface {
    private static final NetworkTable table;
    private static final NetworkTableEntry paramsEntry;
    private static final NetworkTableEntry modesEntry;
    private static final NetworkTableEntry keySetterEntry;
    private static final NetworkTableEntry classSetterEntry;
    private static final NetworkTableEntry modeSetterEntry;
    private static final NetworkTableEntry rawEntry;

    private static final NetworkTableEntry currentModeEntry;
    private static final NetworkTable profiling;
    static boolean hasInitialized = false;

    static {
        table = NetworkTableInstance.getDefault().getTable("OxConfig");
        paramsEntry = table.getEntry("Params");
        modesEntry = table.getEntry("Modes");
        keySetterEntry = table.getEntry("KeySetter");
        classSetterEntry = table.getEntry("ClassSetter");
        modeSetterEntry = table.getEntry("ModeSetter");
        rawEntry = table.getEntry("Raw");
        currentModeEntry = table.getEntry("CurrentMode");
        profiling = table.getSubTable("Profiling");
    }

    static void initialize() {
        hasInitialized = true;
        keySetterEntry.setString("");
        classSetterEntry.setString("");
        modeSetterEntry.setString("");
        modesEntry.setString(String.join(",", ModeSelector.modes));
    }

    static void setProfilingTime(String key, double time) {
        profiling.getEntry(key).setDouble(time);
    }

    static String getSetKeys() {
        String key = keySetterEntry.getString("");
        if (!key.isEmpty()) {
            keySetterEntry.setString("");
        }
        return key;
    }

    static String getSetClasses() {
        String key = classSetterEntry.getString("");
        if (!key.isEmpty()) {
            classSetterEntry.setString("");
        }
        return key;
    }

    static String getSetModes() {
        String mode = modeSetterEntry.getString("");
        if (!mode.isEmpty()) {
            modeSetterEntry.setString("");
        }
        return mode;
    }

    static void updateClasses(Map<String, ConfigurableClass> configurableClasses) {
        JSONArray classes = new JSONArray();
        for (String configClassKey : configurableClasses.keySet()) {
            JSONArray classArr = new JSONArray();
            ConfigurableClass configClass = configurableClasses.get(configClassKey);
            classArr.put(configClass.getPrettyName());
            classArr.put(configClassKey);

            List<ConfigurableClassParam<?>> parameters = configClass.getParameters();
            for (ConfigurableClassParam<?> param : parameters) {
                JSONArray paramArr = new JSONArray();
                paramArr.put(param.getPrettyName());
                String key = param.getKey();
                paramArr.put(key);
                paramArr.put(param.get().getClass().getSimpleName());
                for (String mode : ModeSelector.modes) {
                    JSONObject curModeMap = JsonUtils.getModeMap(mode);
                    paramArr.put(JsonUtils.getRealValue(curModeMap, key, param.shouldStoreComment()));
                }
                classArr.put(paramArr);
            }
            classes.put(classArr);
        }
        table.getEntry("Classes").setString(classes.toString());
    }

    static void updateParameters(Map<String, ConfigurableParameter<?>> parameters) {
        JSONArray params = new JSONArray();
        for (String paramKey : parameters.keySet()) {
            if (paramKey.equalsIgnoreCase("root/mode")) {
                continue;
            }
            JSONArray paramArr = new JSONArray();
            ConfigurableParameter<?> param = parameters.get(paramKey);
            paramArr.put(paramKey);

            // The comment should be the same for all modes. Just pick one, doesn't matter.
            JSONObject nested = JsonUtils.getModeMap(OxConfig.modeSelector.getMode());
            JSONObject data = JsonUtils.getJSONObject(nested, paramKey);
            if (!data.has("comment")) {
                System.out.println(paramKey);
            }
            paramArr.put(data.getString("comment"));
            // Put the type of the parameter
            paramArr.put(param.get().getClass().getSimpleName());
            for (String mode : ModeSelector.modes) {
                JSONObject curModeNested = JsonUtils.getModeMap(mode);
                paramArr.put(JsonUtils.getRealValue(curModeNested, paramKey, param.shouldStoreComment()));
            }
            params.put(paramArr);
        }
        paramsEntry.setString(params.toString());
    }

    static void updateRaw(String raw) {
        String timestamp = String.valueOf(new Date().getTime());
        rawEntry.setString(timestamp + "," + raw);
    }

    static void updateMode() {
        currentModeEntry.setString(OxConfig.modeSelector.getMode());
    }
}
